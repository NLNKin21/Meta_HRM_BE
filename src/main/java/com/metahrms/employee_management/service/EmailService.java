package com.metahrms.employee_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.Map;
import lombok.RequiredArgsConstructor;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Autowired
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendPayrollEmail(String to, Map<String, Object> data) {
        try {
            Context context = new Context();
            context.setVariables(data);

            String htmlContent = templateEngine.process("payroll-template", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Bảng lương tháng " + data.get("month") + "/" + data.get("year"));
            helper.setText(htmlContent, true);

            emailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send payroll email", e);
        }
    }


    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        emailSender.send(message);
    }

    public void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        emailSender.send(message);
    }
}
