package com.kh.dtos;

import java.util.Date;

import com.kh.enums.UserRole;
import org.hibernate.usertype.UserType;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kh.pojo.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO dùng để đăng ký thông tin bệnh nhân.
 * Chứa các trường dữ liệu cần thiết khi tạo tài khoản bệnh nhân mới.
 *
 * <p>
 * Dữ liệu đầu vào:
 * <ul>
 * <li>{@code username}: String - tên đăng nhập</li>
 * <li>{@code password}: String - mật khẩu</li>
 * <li>{@code email}: String - email hợp lệ</li>
 * <li>{@code phone}: String - số điện thoại, tối đa 10 ký tự</li>
 * <li>{@code firstName}: String - họ</li>
 * <li>{@code lastName}: String - tên</li>
 * <li>{@code birthDate}: Date - ngày sinh</li>
 * <li>{@code gender}: String - giới tính</li>
 * <li>{@code address}: String - địa chỉ (không bắt buộc)</li>
 * <li>{@code avatar}: MultipartFile - ảnh đại diện (không bắt buộc)</li>
 * </ul>
 */
public class UserDTO {

    // ATTRIBUTE

    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    @JsonIgnore
    private String password;

    @NotBlank
    @JsonIgnore
    private String confirmPassword;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 10)
    private String phone;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    @NotBlank
    private String gender;

    private String address; // không bắt buộc

    private UserRole userRole;

    @JsonIgnore
    private MultipartFile avatarUpload; // Không bắt buộc

    private String avatar; // Người dùng không tự động cập nhật được, tự động tạo khi có avatarUpload

    // CONSTRUCTOR

    public UserDTO() {

    }

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.birthDate = (Date) user.getBirthDate();
        this.gender = user.getGender();
        this.address = user.getAddress();
        this.avatar = user.getAvatar();
        this.userRole = user.getRole();
    }

    public User toObject(UserRole userRole, String hashedPassword) {
        User user = new User();
        user.setRole(userRole);
        user.setUsername(this.getUsername());
        user.setPassword(hashedPassword);
        user.setFirstName(this.getFirstName());
        user.setLastName(this.getLastName());
        user.setEmail(this.getEmail());
        user.setPhone(this.getPhone());
        user.setAddress(this.getAddress());
        user.setBirthDate(this.getBirthDate());
        user.setGender(this.getGender());

        return user;
    }

    public User toObject(UserRole userRole, String hashedPassword, String uploadedAvatarUrl) {
        User user = this.toObject(userRole, hashedPassword);
        user.setAvatar(uploadedAvatarUrl);
        return user;
    }

    // GETTER/SETTER

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public MultipartFile getAvatarUpload() {
        return avatarUpload;
    }

    public void setAvatarUpload(MultipartFile avatarUpload) {
        this.avatarUpload = avatarUpload;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
