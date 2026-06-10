package com.metahrms.employee_management.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.metahrms.employee_management.dto.request.User.ChangePasswordDto;
import com.metahrms.employee_management.dto.request.User.LoginDto;
import com.metahrms.employee_management.entity.User;
import com.metahrms.employee_management.enums.UserRole;
import com.metahrms.employee_management.enums.UserStatus;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
@SuppressWarnings("unused")
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    UserRepository userRepository;
    EmailService emailService;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public String authenticate(LoginDto request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Email hoặc mật khẩu không chính xác"));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isAuthenticated) {
            throw new BusinessException("Email hoặc mật khẩu không chính xác");
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException("Tài khoản đã bị xóa. Vui lòng liên hệ quản trị viên");
        } else if (user.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException("Tài khoản đang bị vô hiệu hóa. Vui lòng liên hệ quản trị viên");
        } else if (user.getStatus() == UserStatus.PENDING) {
            throw new BusinessException("Tài khoản chưa được kích hoạt. Vui lòng liên hệ quản trị viên");
        }

        return generateToken(user.getId(), user.getEmail(), user.getUsername(), user.getRole());
    }

    private String generateToken(Integer id, String email, String username, UserRole role) {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .issuer("EmployeeManagement.com")
                    .issueTime(new Date())
                    .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli())) 
                    .claim("id", String.valueOf(id))
                    .claim("email", email)
                    .claim("username", username)
                    .claim("role", role)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void changePassword(HttpServletRequest request, ChangePasswordDto dto) {

        // ── 1. Validate input ──────────────────────────────────
        if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()) {
            throw new RuntimeException("Mật khẩu hiện tại không được để trống");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new RuntimeException("Mật khẩu mới không được để trống");
        }
        if (dto.getNewPassword().length() < 8) {
            throw new RuntimeException("Mật khẩu mới phải có ít nhất 8 ký tự");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu không khớp");
        }

        // ── 2. Lấy user hiện tại từ request ───────────────────
        Object userObj = request.getAttribute("user");
        if (!(userObj instanceof Map)) {
            throw new RuntimeException("Không tìm thấy thông tin người dùng");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> userMap = (Map<String, Object>) userObj;
        Integer userId = Integer.valueOf(userMap.get("id").toString());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // ── 3. Kiểm tra mật khẩu hiện tại ─────────────────────
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        // ── 4. Không cho đổi sang mật khẩu cũ ─────────────────
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng mật khẩu hiện tại");
        }

        // ── 5. Hash & lưu mật khẩu mới ────────────────────────
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        // ── 1. Tìm user theo email ─────────────────────────
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                    "Không tìm thấy tài khoản với email: " + email));

        // ── 2. Kiểm tra trạng thái tài khoản ──────────────
        if (user.getStatus() == UserStatus.DELETED) {
            throw new RuntimeException("Tài khoản này đã bị xóa");
        }
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new RuntimeException("Tài khoản này đang bị vô hiệu hóa");
        }

        // ── 3. Generate password mới ───────────────────────
        String rawPassword = generateRandomPassword();

        // ── 4. Hash và lưu vào DB ──────────────────────────
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        // ── 5. Gửi email ───────────────────────────────────
        sendPasswordResetEmail(user, rawPassword);
    }



    private String generateRandomPassword() {
        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String numberChars = "0123456789";
        String specialChars = "!@#$%^&*";

        String allChars = upperCaseChars + lowerCaseChars + numberChars + specialChars;
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // Ensure password has at least one character from each category
        password.append(upperCaseChars.charAt(random.nextInt(upperCaseChars.length())));
        password.append(lowerCaseChars.charAt(random.nextInt(lowerCaseChars.length())));
        password.append(numberChars.charAt(random.nextInt(numberChars.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Fill the rest randomly (total length: 12 characters)
        for (int i = 4; i < 12; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password to randomize character positions
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    private void sendPasswordResetEmail(User user, String newPassword) {
        String subject = "Password Reset Request";
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Password Reset</h2>"
                + "<p>Your new temporary password is:</p>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">"
                + newPassword + "</p>"
                + "<p style=\"color: #666;\">Please change this password after logging in.</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Chỉ log, không throw để tránh ảnh hưởng luồng chính
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
