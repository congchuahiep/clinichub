package com.kh.utils;

import java.util.Date;

import com.kh.enums.UserRole;
import com.kh.pojo.User;
import com.kh.repositories.UserRepository;
import com.kh.services.UserService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Lớp này cung cấp các hành vi cho việc xác thực người dùng bằng JWT Token
 */
@Component
public class SecurityUtils {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    private static final String SECRET = "12345678901234567890123456789012"; // 32 ký tự (AES key)
    private static final long EXPIRATION_MS = 86400000; // 1 ngày

    /**
     * Phương thức này tạo một JWT chứa tên người dùng, thời gian phát hành, thời
     * gian hết hạn, ký bằng thuật toán HMAC SHA-256 và trả về chuỗi token đã ký
     * 
     * @param username - Tên người dùng cần tạo token
     * @return - Chuỗi JWT token đã được serialize
     */
    public String generateToken(String username) {
        try {
            JWSSigner signer = new MACSigner(SECRET); // Nhập khoá bí mật để làm đối tượng ký

            // Tạo thông tin cho token
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username) // Đặt chủ sở hữu của token
                    .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_MS)) // Đặt thời hạn kết thúc token
                    .issueTime(new Date())
                    .build();

            // Tạo đối tượng "được ký JWT" với bộ mã hoá HS256
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet);

            signedJWT.sign(signer); // Ký JWT bằng khóa bí mật

            return signedJWT.serialize(); // Chuyển JWT đã ký thành chuỗi để trả về cho client
        } catch (Exception e) {
            throw new SecurityException("Lỗi khi tạo JWT!");
        }
    }

    /**
     * Xác thực JWT token: kiểm tra chữ ký hợp lệ và token còn hạn sử dụng.
     * Nếu hợp lệ, trả về tên người dùng (subject) từ token; nếu không hợp lệ hoặc
     * hết hạn, trả về null.
     *
     * @param token Chuỗi JWT token cần xác thực
     * @return Tên người dùng nếu token hợp lệ, hoặc null nếu token không hợp lệ/hết
     *         hạn
     * @throws Exception Nếu có lỗi khi phân tích hoặc xác thực token
     */
    public UserDetails validateTokenAndGetUsername(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token); // Chuyển token tành đối tượng "được ký JWT"
        JWSVerifier verifier = new MACVerifier(SECRET); // Kiểm tra JWT có được ký bằng khoá bí mật hay không

        boolean isVerifier = signedJWT.verify(verifier); // Kiểm tra token có hợp lệ

        // Token không hợp lệ
        if (!isVerifier)
            return null;

        // Token hết hạn sử dụng
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!expiration.after(new Date())) {
            return null;
        }

        String username = signedJWT.getJWTClaimsSet().getSubject();
        return userService.loadUserByUsername(username);
    }

    /**
     * Kiểm tra xem người dùng hiện tại có ROLE tương ứng với UserType hay không
     */
    public boolean hasRole(Authentication auth, UserRole userRole) {
        if (auth == null || !auth.isAuthenticated()) return false;

        String requiredRole = "ROLE_" + userRole.name();

        System.out.println("Required role: " + requiredRole);
        System.out.println("Authorities: " + auth.getAuthorities());

        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(requiredRole));
    }

    /**
     * Kiểm tra và ném lỗi nếu không có quyền
     */
    public void requireRole(Authentication auth, UserRole userRole) {
        if (!hasRole(auth, userRole)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập vào trang này!");
        }
    }

    public Long getCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        String username = authentication.getName();
        // Lấy user từ database dựa vào username
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElse(null);
    }

}
