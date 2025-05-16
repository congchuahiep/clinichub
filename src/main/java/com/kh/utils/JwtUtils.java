package com.kh.utils;

import java.util.Date;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Lớp này cung cấp các hành vi cho việc xác thực người dùng bằng JWT Token
 */
public class JwtUtils {

    private static final String SECRET = "12345678901234567890123456789012"; // 32 ký tự (AES key)
    private static final long EXPIRATION_MS = 86400000; // 1 ngày

    /**
     * Phương thức này tạo một JWT chứa tên người dùng, thời gian phát hành, thời
     * gian hết hạn, ký bằng thuật toán HMAC SHA-256 và trả về chuỗi token đã ký
     * 
     * @param username - Tên người dùng cần tạo token
     * @return - Chuỗi JWT token đã được serialize
     * @throws Exception Nếu có lỗi khi tạo token
     */
    public static String generateToken(String username) throws Exception {
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
    public static String validateTokenAndGetUsername(String token) throws Exception {
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

        return signedJWT.getJWTClaimsSet().getSubject();
    }
}
