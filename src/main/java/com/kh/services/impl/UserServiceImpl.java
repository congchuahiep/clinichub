package com.kh.services.impl;

import com.kh.dtos.DoctorLicenseDTO;
import com.kh.dtos.DoctorProfileDTO;
import java.util.HashSet;
import java.util.Set;

import com.kh.exceptions.FileUploadException;
import com.kh.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.dtos.UserDTO;
import com.kh.enums.UserRole;
import com.kh.pojo.DoctorLicense;
import com.kh.pojo.User;
import com.kh.repositories.DoctorLisenceRepository;
import com.kh.repositories.UserRepository;
import com.kh.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DoctorLisenceRepository doctorRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FileUploadUtils fileUploadUtils;

    public void authenticate(String username, String password) {
        User u = this.userRepository.getUserByUsername(username);

        if (!this.passwordEncoder.matches(password, u.getPassword())) {
            throw new BadCredentialsException("Tài khoản hoặc mật khẩu không chính xác!");
        }

    }

    /**
     * Khi người dùng đăng nhập, Spring Security sẽ tự động gọi loadUserByUsername
     * để lấy thông tin người dùng và quyền
     *
     * @param username - Tên người dùng cần được load
     * @return Trả về một đối tượng User của Spring Security (không phải pojo
     * User) chứa username, password và danh sách quyền. Đối tượng này
     * implements UserDetails nên Spring Security có thể sử dụng để
     * xác thực và phân quyền
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
    public UserDTO addPatientUser(UserDTO patientDTO) throws FileUploadException {
        try {
            // KIỂM TRA MẬT KHẨU XÁC NHẬN
            if (!patientDTO.getPassword().equals(patientDTO.getConfirmPassword())) {
                throw new RuntimeException("Mật khẩu xác nhận không khớp với mật khẩu của bạn!");
            }

            // MÃ HOÁ MẬT KHẨU
            String hashedPassword = this.passwordEncoder.encode(patientDTO.getPassword());

            // UPLOAD ẢNH LÊN CLOUDINARY
            String uploadedAvatarUrl = patientDTO.getAvatarUpload() != null ?
                    fileUploadUtils.uploadFile(patientDTO.getAvatarUpload()) : null;

            // CHUYỂN DTO THÀNH OBJECT
            User patient = patientDTO.toObject(patientDTO, UserRole.PATIENT, hashedPassword, uploadedAvatarUrl);

            // TIẾN HÀNH LƯU USER VÀO TRONG DATABASE
            User savedUser = this.userRepository.addUser(patient);
            patientDTO.setAvatar(savedUser.getAvatar());
            return patientDTO;

        } catch (FileUploadException e) {
            throw new FileUploadException("Không thể tải ảnh lên!");
        }
    }


    @Override
    public UserDTO getUserByUsername(String username) {
        User user = this.userRepository.getUserByUsername(username);

        return new UserDTO(user.getUsername(), user.getPassword());
    }
    
    @Override
    public DoctorProfileDTO addDoctorUser(UserDTO doctorDTO, DoctorLicenseDTO doctorLicenseDTO) throws FileUploadException{
       try {
            // KIỂM TRA MẬT KHẨU XÁC NHẬN
            if (!doctorDTO.getPassword().equals(doctorDTO.getConfirmPassword())) {
                throw new RuntimeException("Mật khẩu xác nhận không khớp với mật khẩu của bạn!");
            }

            
            
            // MÃ HOÁ MẬT KHẨU
            String hashedPassword = this.passwordEncoder.encode(doctorDTO.getPassword());

            // UPLOAD ẢNH LÊN CLOUDINARY
            String uploadedAvatarUrl = doctorDTO.getAvatarUpload() != null ?
                    fileUploadUtils.uploadFile(doctorDTO.getAvatarUpload()) : null;

            // CHUYỂN DTO THÀNH OBJECT
            User doctor = doctorDTO.toObject(doctorDTO, UserRole.DOCTOR, hashedPassword, uploadedAvatarUrl);
            
            // TODO: Thêm chứng chỉ 
            
            
            // TIẾN HÀNH LƯU USER VÀO TRONG DATABASE
            User savedUser = this.userRepository.addUser(doctor);
            doctorDTO.setAvatar(savedUser.getAvatar());
            
            DoctorLicense doctorLicense = doctorRepository.addDoctorLisence(doctorLicenseDTO.toObject(savedUser));
            doctorLicenseDTO.setDoctorId(doctorLicense.getId());
            
            DoctorProfileDTO doctorProfileDTO = new DoctorProfileDTO(doctorDTO, doctorLicenseDTO);
            
            return doctorProfileDTO;

        } catch (FileUploadException e) {
            throw new FileUploadException("Không thể tải ảnh lên!");
        }
    }
}
