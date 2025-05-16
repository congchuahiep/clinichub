package com.kh.services.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kh.dtos.UserDTO;
import com.kh.enums.UserRole;
import com.kh.pojo.User;
import com.kh.repositories.UserRepository;
import com.kh.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

    public boolean authenticate(String username, String password) {
        User u = this.userRepository.getUserByUsername(username);

        if (!this.passwordEncoder.matches(password, u.getPassword())) {
            throw new BadCredentialsException("Tài khoản hoặc mật khẩu không chính xác!");
        }

        return true;
    }

    /**
     * Khi người dùng đăng nhập, Spring Security sẽ tự động gọi loadUserByUsername
     * để lấy thông tin người dùng và quyền
     * 
     * @param username - Tên người dùng cần được load
     * @return Trả về một đối tượng User của Spring Security (không phải pojo
     *         User) chứa username, password và danh sách quyền. Đối tượng này
     *         implements UserDetails nên Spring Security có thể sử dụng để
     *         xác thực và phân quyền
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.getUserByUsername(username); // Lấy User từ Database

        // Nếu không tìm thấy user, quăng ngoại lệ
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username!");
        }

        // Tạo danh sách quyền (authorities) cho người dùng. Ở đây, quyền được lấy từ
        // trường role của user (ADMIN, DOCTOR, PATIENT).
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

        // Trả về một đối tượng User của Spring Security (không phải com.kh.pojo.User),
        // chứa username, password và danh sách quyền. Đối tượng này implements
        // UserDetails nên Spring Security có thể sử dụng để xác thực và phân quyền
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public UserDTO addPatientUser(UserDTO patientDTO) {

        if (!patientDTO.getPassword().equals(patientDTO.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp với mật khẩu của bạn!");
        }

        User patient = new User();

        patient.setRole(UserRole.PATIENT);

        patient.setUsername(patientDTO.getUsername());
        patient.setPassword(this.passwordEncoder.encode(patientDTO.getPassword()));

        patient.setFirstName(patientDTO.getFirstName());
        patient.setLastName(patientDTO.getLastName());
        patient.setEmail(patientDTO.getEmail());
        patient.setPhone(patientDTO.getPhone());
        patient.setAddress(patientDTO.getAddress());
        patient.setBirthDate(patientDTO.getBirthDate());
        patient.setGender(patientDTO.getGender());

        MultipartFile avatar = patientDTO.getAvatarUpload();

        // Xử lý đăng tải ảnh lên cloudinary và lấy đường link đã được đăng tải gắn vào
        // avatar người dùng
        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(
                        avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));

                Object secureUrl = uploadResult.get("secure_url");

                if (secureUrl != null) {
                    patient.setAvatar(secureUrl.toString());
                }
            } catch (Exception e) {
                throw new RuntimeException("Không gửi được ảnh: " + e.getMessage()); // Quăng ngoại lệ nếu không lưu ảnh
                                                                                     // được
            }
        }

        // TIẾN HÀNH LƯU USER VÀO TRONG DATABASE
        User savedUser = this.userRepository.addUser(patient);

        patientDTO.setAvatar(savedUser.getAvatar());

        return patientDTO;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = this.userRepository.getUserByUsername(username);

        return new UserDTO(user.getUsername(), user.getPassword());
    }
}
