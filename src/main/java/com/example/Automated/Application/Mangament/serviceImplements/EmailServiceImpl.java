package com.example.Automated.Application.Mangament.serviceImplements;

import com.example.Automated.Application.Mangament.exception.AppException;
import com.example.Automated.Application.Mangament.exception.ErrorCode;
import com.example.Automated.Application.Mangament.serviceInterfaces.EmailServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailServiceInterface {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.sender}")
    private String senderEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    private void sendEmail(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isBlank()) {
            throw new AppException(ErrorCode.EMAIL_NULL);
        }
        if (!toEmail.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+[.])+[\\w]+[\\w]$")) {
            throw new AppException(ErrorCode.EMAIL_INVALID);
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);  // HTML enabled
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.EMAIL_CAN_NOT_SEND);
        }
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "OTP Verification";
        String body = "<html><body><h2>Your OTP Code</h2><p style='font-size:18px; color:blue;'><b>" + otp + "</b></p><p>Expires in 5 minutes. Do not share!</p></body></html>";
        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendNotification(String toEmail, String subject, String body) {
        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendResetPasswordLink(String toEmail, String resetToken) {
        String subject = "Reset Password ";
        String body = "<html><body><h2>Reset Your Password</h2><p>Click the link below to reset:</p><a href='http://automatedmangament.asia/reset?token=" + resetToken + "' style='color:green;'>Reset Password</a><p>Link expires in 1 hour.</p></body></html>";
        sendEmail(toEmail, subject, body);
    }
}