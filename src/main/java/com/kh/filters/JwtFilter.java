package com.kh.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kh.utils.SecurityUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityUtils securityUtils;

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
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain chain)

    throws IOException, ServletException {
        // Xác định prefix của các endpoint cần bảo vệ (/api/secure) và lấy URI của request hiện tại
        String apiPrefix = request.getContextPath() + "/api/secure";
        String requestUri = request.getRequestURI();

        // Chỉ những request bắt đầu bằng (/api/secure) thì mới kiểm tra
        if (requestUri.startsWith(apiPrefix)) {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
                return;
            }

            String token = header.substring(7);
            try {
                UserDetails user = securityUtils.validateTokenAndGetUsername(token);
                if (user != null) {
                    request.setAttribute("username", user);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    chain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                // Log lỗi nếu cần
            }
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ hoặc hết hạn");
            return;
        }

        chain.doFilter(request, response);
    }
}