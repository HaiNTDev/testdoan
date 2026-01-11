package com.example.Automated.Application.Mangament.serviceImplements;


import com.example.Automated.Application.Mangament.model.Account;
import com.example.Automated.Application.Mangament.repositories.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtServiceImplement {
    private static final String privateKey = "Kdsaflkj2kjf23uf9ds8dsakgnsa@Gsakljg";

    private static final long TEMP_TOKEN_VALIDITY = 1000 * 60 * 60 * 5;

    @Autowired
    private AccountRepository accountRepository;

    private Key getSignKey(){return Keys.hmacShaKeyFor(privateKey.getBytes());}

    public String generateToken(String userName){
        Map<String, String> claims = new HashMap<>();
        Account account = accountRepository.findByUserName(userName).get();
        if(account.getDepartment() != null){
            long departmentId = account.getDepartment().getId();
            claims.put("departmentId", String.valueOf(departmentId));
            claims.put("departmentName", account.getDepartment().getDepartmentName());
        }
        String roleName = account.getRole().getRoleName().toString();
        claims.put("role", roleName);
        claims.put("gmail", account.getGmail());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, String userDetails, String role) {
        final String username = extractUsername(token);
        final String extractedRole = extractRole(token);
        return (username.equals(userDetails) && extractedRole.equals(role) && !isTokenExpired(token));
    }

    public Date extractExpiration(String token) {
        // BƯỚC 1: Xây dựng Bộ giải mã (Parser) với Khóa Bí mật
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()

                // BƯỚC 2: Giải mã và Xác minh Chữ ký (Bảo mật)
                .parseClaimsJws(token)

                // BƯỚC 3: Lấy ra Payload (Dữ liệu)
                .getBody();

        // BƯỚC 4: Lấy giá trị của trường 'expiration'
        return claims.getExpiration();
    }


    public String generate2faToken(String username, String email) {

        // 1. Định nghĩa các Claims (Thông tin)
        Map<String, Object> claims = new HashMap<>();

        // Đánh dấu đây là token cho quy trình 2FA
        claims.put("token_type", "2FA_PENDING");

        // Lưu email (cần thiết cho API verify-otp)
        claims.put("email", email);

        // 2. Thời gian tạo và hết hạn
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + TEMP_TOKEN_VALIDITY);

        // 3. Xây dựng Token JWT
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username) // Subject vẫn là username
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Sử dụng khóa bí mật của bạn
                .compact();
    }


    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }


    public String extractUsername(String token) {
        // BƯỚC 1: Xây dựng Bộ giải mã (Parser) với Khóa Bí mật
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()

                // BƯỚC 2: Giải mã và Xác minh Chữ ký (Bảo mật)
                .parseClaimsJws(token)

                // BƯỚC 3: Lấy ra Payload (Dữ liệu)
                .getBody();

        // BƯỚC 4: Lấy giá trị của trường 'subject'
        return claims.getSubject();
    }

    public String extractGmail (String token) {
        // BƯỚC 1: Xây dựng Bộ giải mã (Parser) với Khóa Bí mật
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()

                // BƯỚC 2: Giải mã và Xác minh Chữ ký (Bảo mật)
                .parseClaimsJws(token)

                // BƯỚC 3: Lấy ra Payload (Dữ liệu)
                .getBody();

        // BƯỚC 4: Lấy giá trị của trường tùy chỉnh 'role'
        return claims.get("gmail", String.class);
    }

    public String extractRole(String token) {
        // BƯỚC 1: Xây dựng Bộ giải mã (Parser) với Khóa Bí mật
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()

                // BƯỚC 2: Giải mã và Xác minh Chữ ký (Bảo mật)
                .parseClaimsJws(token)

                // BƯỚC 3: Lấy ra Payload (Dữ liệu)
                .getBody();

        // BƯỚC 4: Lấy giá trị của trường tùy chỉnh 'role'
        return claims.get("role", String.class);
    }
}
