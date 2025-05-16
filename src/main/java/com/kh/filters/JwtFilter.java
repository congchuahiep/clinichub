package com.kh.filters;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kh.utils.JwtUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter implements Filter {

    /**
     * Ghi đè phương thức doFilter để xử lý từng request đi qua filter này
     * 
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Ép kiểu request và response về dạng HTTP để sử dụng các phương thức đặc trưng
        // của HTTP
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Xác định prefix của các endpoint cần bảo vệ (/api/secure) và lấy URI của request hiện tại
        String apiPrefix = httpRequest.getContextPath() + "/api/secure";
        String requestUri = httpRequest.getRequestURI();

        // Chỉ những request bắt đầu bằng (/api/secure) thì mới kiểm tra
        if (requestUri.startsWith(apiPrefix)) {
            String header = httpRequest.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
                return;
            }

            String token = header.substring(7);
            try {
                String username = JwtUtils.validateTokenAndGetUsername(token);
                if (username != null) {
                    httpRequest.setAttribute("username", username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    chain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                // Log lỗi nếu cần
            }
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ hoặc hết hạn");
            return;
        }

        chain.doFilter(request, response);
    }
}