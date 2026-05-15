package com.metahrms.employee_management.service;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

     /**
     * Gửi email plain text tổng quát
     * Dùng cho: tuyển dụng, thông báo, nhắc nhở, v.v.
     * Không throw exception → service gọi không bị crash
     */
    public void sendEmail(String toEmail, String subject, String textContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(textContent, false);

            mailSender.send(message);
            log.info("Email sent to: {} | Subject: {}", toEmail, subject);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Gửi email chào mừng kèm thông tin đăng nhập
     * Nếu gửi thất bại → chỉ log, không throw exception
     */
    public void sendWelcomeEmail(String toEmail, String username, String rawPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[Meta HRM] Tài khoản của bạn đã được tạo");
            message.setText(buildWelcomeEmailContent(username, toEmail, rawPassword));

            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);

        } catch (MailException e) {
            // Không throw exception → user vẫn được tạo dù mail lỗi
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Nội dung email plain text
     */
    private String buildWelcomeEmailContent(String username, String email, String rawPassword) {
        return """
            Xin chào %s,

            Tài khoản của bạn trên hệ thống Meta HRM đã được tạo thành công.

            Thông tin đăng nhập:
            ──────────────────────────
            Email    : %s
            Mật khẩu : %s
            ──────────────────────────

            Vui lòng đăng nhập và đổi mật khẩu ngay sau lần đầu tiên sử dụng.

            Trân trọng,
            Meta HRM System
            """.formatted(username, email, rawPassword);
    }

    /**
     * Gửi email HTML tổng quát (dùng cho reset password, verify, v.v.)
     */
    public void sendVerificationEmail(String toEmail, String subject, String htmlContent) 
            throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = isHtml

            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new MessagingException("Failed to send email: " + e.getMessage());
        }
    }
}