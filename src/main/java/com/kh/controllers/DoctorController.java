package com.kh.controllers;

import com.kh.dtos.DoctorProfileDTO;
import com.kh.dtos.UserDTO;
import com.kh.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
//@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private UserService userService;

    /**
     * Hiển thị danh sách bác sĩ.
     *
     * @param model Model để gửi dữ liệu lên giao diện
     * @return Tên view danh sách bác sĩ
     */
    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        Map<String, String> params = new HashMap<>();
        // Nếu có request param, thêm vào `params`
        params.put("isFalse", "1");

        // Lấy danh sách bác sĩ
        model.addAttribute("doctors", userService.getDoctorsWithoutRating(params).getResults());
        return "doctor-list";
    }

    @PostMapping("/doctors/approve/{id}")
    public String approveDoctor(@PathVariable("id") Long id) {
        userService.approveDoctor(id); // Cập nhật trong DB
        return "redirect:/doctors"; // Quay lại trang danh sách
    }

}