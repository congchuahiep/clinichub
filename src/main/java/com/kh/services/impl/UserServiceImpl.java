package com.kh.services.impl;

import com.kh.dtos.DoctorLicenseDTO;
import com.kh.dtos.DoctorProfileDTO;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.kh.dtos.DoctorWithRating;
import com.kh.utils.PaginatedResult;
import com.kh.exceptions.FileUploadException;
import com.kh.pojo.Hospital;
import com.kh.pojo.Specialty;
import com.kh.repositories.HospitalRepository;
import com.kh.repositories.SpecialtyRepository;
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
import com.kh.pojo.HealthRecord;
import com.kh.pojo.User;
import com.kh.repositories.DoctorLisenceRepository;
import com.kh.repositories.HealthRecordRepository;
import com.kh.repositories.UserRepository;
import com.kh.services.UserService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorLisenceRepository doctorLisenceRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FileUploadUtils fileUploadUtils;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private HospitalRepository hospitalRepository1;

    @Override
    public void authenticate(String username, String password) {
        User user = this.userRepository
                .findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Tài khoản hoặc mật khẩu không chính xác!"));

        if (!this.passwordEncoder.matches(password, user.getPassword())) {
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
        // Lấy User từ Database
        User user = this.userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại!"));

        // Tạo danh sách quyền (authorities) cho người dùng. Ở đây, quyền được lấy từ
        // trường role của user (ADMIN, DOCTOR, PATIENT).
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

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
            User patient = patientDTO.toObject(UserRole.PATIENT, hashedPassword, uploadedAvatarUrl);

            // TIẾN HÀNH LƯU USER VÀO TRONG DATABASE
            User savedUser = this.userRepository.save(patient);

            // TẠO VÀ LƯU HEALTH RECORD RỖNG CHO USER MỚI TẠO
            HealthRecord healthRecord = new HealthRecord();
            healthRecord.setPatient(savedUser);
            healthRecord.setMedicalHistory("");
            healthRecord.setAllergies("");
            healthRecord.setChronicConditions("");
            healthRecord.setCreatedAt(new java.util.Date());
            healthRecord.setUpdatedAt(new java.util.Date());

            healthRecordRepository.save(healthRecord);

            // CẬP NHẬT URL AVATAR TRẢ VỀ DTO
            patientDTO.setAvatar(savedUser.getAvatar());

            return patientDTO;

        } catch (FileUploadException e) {
            throw new FileUploadException("Không thể tải ảnh lên!");
        }
    }


    @Override
    public UserDTO getUserByUsername(String username) {
        User user = this.userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại!"));

        return new UserDTO(user);
    }

    @Override
    @Transactional
    public DoctorProfileDTO addDoctorUser(UserDTO doctorDTO, DoctorLicenseDTO doctorLicenseDTO, Long hospitalId) throws FileUploadException {
        try {
            // KIỂM TRA MẬT KHẨU XÁC NHẬN
            if (!doctorDTO.getPassword().equals(doctorDTO.getConfirmPassword())) {
                throw new RuntimeException("Mật khẩu xác nhận không khớp với mật khẩu của bạn!");
            }

            // Kiểm tra và lấy specialist
            Specialty specialty = specialtyRepository.findById(doctorLicenseDTO.getSpecialtyId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên khoa với ID: " + doctorLicenseDTO.getSpecialtyId()));

            // Kiểm tra và lấy hospital
            Hospital hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh viện với ID: " + hospitalId));

            // MÃ HOÁ MẬT KHẨU
            String hashedPassword = this.passwordEncoder.encode(doctorDTO.getPassword());

            // UPLOAD ẢNH LÊN CLOUDINARY
            String uploadedAvatarUrl = doctorDTO.getAvatarUpload() != null ?
                    fileUploadUtils.uploadFile(doctorDTO.getAvatarUpload()) : null;

            // Lưu thông tin bác sĩ
            User doctor = doctorDTO.toObject(UserRole.DOCTOR, hashedPassword, uploadedAvatarUrl);
            doctor.setIsActive(false); // Tài khoản bác sĩ mới tạo mặc định chưa được kích hoạt

            // Lưu thông tin giấy phép hành nghề của bác sĩ
            Set<DoctorLicense> doctorLicenses = new HashSet<>();
            doctorLicenses.add(doctorLicenseDTO.toObject(doctor, specialty));
            doctor.setDoctorLicenseSet(doctorLicenses);
            // Tự động lưu doctor license vào database được vì nó là quan hệ many-to-one đúng hơn là do user "sở hữu"
            // doctor license. Tức khi lưu một user đang ở trạng thái transient, mà user đó có chứa một doctor license
            // cũng đang ở trạng thái transient, thì khi hibernate lưu user cũng đồng thời lưu doctor license luôn
            User savedUser = this.userRepository.save(doctor);

            // Lưu bệnh viện mà bác sĩ khám
            Set<Hospital> hospitals = new HashSet<>();
            hospitals.add(hospital);
            doctor.setHospitalSet(hospitals);
            // Khác với doctor license, doctor không "sở hữu" hospital. Thứ nhất, là do giữa doctor và hospital là mối
            // quan hệ many-to-many. Thứ hai, quan hệ many-to-many ta có thể đặt chiều sở hữu được, nên đúng logic nhất
            // ở đây sẽ phải là hospital sở hữu các doctor
            hospitalRepository.registerDoctorToHospital(hospital, doctor); // Xử lý việc lưu ở chiều sở hữu

            // Cập nhật thông tin vào DTO để trả về
            doctorDTO.setAvatar(savedUser.getAvatar());

            return new DoctorProfileDTO(doctor);

        } catch (FileUploadException e) {
            throw new FileUploadException("Không thể tải ảnh lên!");
        }
    }

    @Override
    public PaginatedResult<DoctorProfileDTO> getDoctors(Map<String, String> params) {
        PaginatedResult<DoctorWithRating> doctors = this.userRepository.doctorList(params);
        return doctors.mapTo(DoctorProfileDTO::new);
    }
}
