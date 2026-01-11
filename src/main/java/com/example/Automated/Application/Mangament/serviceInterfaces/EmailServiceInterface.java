package com.example.Automated.Application.Mangament.serviceInterfaces;

import org.springframework.stereotype.Component;

@Component
public interface EmailServiceInterface {
    void sendOtpEmail(String toEmail, String otp);
    void sendNotification(String toEmail, String subject, String body);
    void sendResetPasswordLink(String toEmail, String resetToken);
}
