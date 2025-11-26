package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.service.EmailService;
import com.familyFirstSoftware.SecureDocAIBackend.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.familyFirstSoftware.SecureDocAIBackend.utils.EmailUtils.getEmailMessage;
import static com.familyFirstSoftware.SecureDocAIBackend.utils.EmailUtils.getResetPasswordMessage;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/10/2024
 *
 * TODO: These two methods probably could be one method.sendNewAccountEmail and sendPasswordResetEmail
 * TODO: Change to rich email with embedded pictures ect.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    private static final String PASSWORD_RESET_REQUEST = "Rest Password Request";
    private final JavaMailSender sender;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendNewAccountEmail(String name, String toEmail, String token) {
        try{
            var message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setText(getEmailMessage(name, host, token));
            sender.send(message);
        }catch (Exception e){
            log.error(e.getMessage());
            log.error("Failed to send new account email"); // TODO: we should not throw an exception here. Probably just resend or ignore and log then wait for user to try again
        }

    }



    @Override
    @Async
    public void sendPasswordResetEmail(String name, String toEmail, String token) {
        try{
            var message = new SimpleMailMessage();
            message.setSubject(PASSWORD_RESET_REQUEST);
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setText(getResetPasswordMessage(name, host, token));
            sender.send(message);
        }catch (Exception e){
            log.error(e.getMessage());
            log.error("Failed to send reset password email"); // TODO: we should not throw an exception here. Probably just resend or ignore and log then wait for user to try again
        }

    }


}

