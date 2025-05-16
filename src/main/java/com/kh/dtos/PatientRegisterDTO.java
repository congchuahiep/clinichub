package com.kh.dtos;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class PatientRegisterDTO {

    // ATTRIBUTE

    @NotBlank
    private String username;

    @NotBlank
    @JsonIgnore
    private String password;

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
    private Date birthDate;

    @NotBlank
    private String gender;

    private String address; // không bắt buộc

    @JsonIgnore
    private MultipartFile avatarUpload; // Không bắt buộc

    private String avatar; // Người dùng không tự động cập nhật được, tự động tạo khi có avatarUpload

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

}
