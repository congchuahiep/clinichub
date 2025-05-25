/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.configs;


import org.springframework.lang.NonNull;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.kh.filters.JwtFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;

/**
 * Lớp khởi tạo SpringMVC thay thế cho khởi tạo bằng tệp `web.xml` truyền thống
 * 
 * @author admin
 */
public class DispatcherServerletInit extends AbstractAnnotationConfigDispatcherServletInitializer {
    /**
     * Xác định các cấu hình cơ sở (root configurations) cho ứng dụng Spring MVC.
     * Các cấu hình này sẽ tạo ra root application context, thường chứa các bean
     * liên quan đến persistence layer, services, etc.
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] {
                ThymeleafConfig.class, // Cấu hình thymeleaf
                HibernateConfigs.class, // Cấu hình Hibernate
                SecurityConfigs.class, // Cấu hình Spring boot Security
                UtilsConfig.class, // Cấu hình các bean tiện ích
                MailConfig.class
        };
    }

    /**
     * Phương thức này xác định các cấu hình cho DispatcherServlet. Cụ thể là
     * cấu hình FrontController
     * 
     * @return `WebAppContextConfigs` - Lớp được chọn làm IoC Container cho
     *         chương trình
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {
                WebAppContextConfigs.class
        };
    }

    /**
     * Cấu hình các tham số cho upload file đa phần (multipart), như vị trí lưu tạm,
     * kích thước file tối đa, tổng dung lượng request tối đa cho DispatcherServlet.
     */
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        String location = "/";
        long maxFileSize = 5242880; // Tổng dung lượng của một file: 5MB
        long maxRequestSize = 20971520; // Tổng dung lượng của một request: 20MB
        int fileSizeThreshold = 0;

        registration.setMultipartConfig(new MultipartConfigElement(
                location,
                maxFileSize,
                maxRequestSize,
                fileSizeThreshold));
    }

    /**
     * Phương thức này xác định pattern URL mapping cho DispatcherServlet.
     * 
     * @return Cấu hình `"/"` - Có nghĩa là DispatcherServlet sẽ xử lý tất cả các
     *         request
     *         đến ứng dụng
     */
    @Override
    @NonNull
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}
