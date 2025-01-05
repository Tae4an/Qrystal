package com.qrystal.app.user.service;

import com.qrystal.exception.CustomException;
import com.qrystal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender emailSender;

    private static final String EMAIL_PREFIX = "EMAIL_VERIFY:";
    private static final long EXPIRATION_TIME = 5;

    public void sendVerificationEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Qrystal 회원가입 이메일 인증");
        message.setText("아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n"
                + "http://127.0.0.1:80/auth/verify?token=" + token);
        emailSender.send(message);
    }

    public void saveVerificationToken(String email, String token) {
        redisTemplate.opsForValue()
                .set(EMAIL_PREFIX + token, email, EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    public Optional<String> getEmailByToken(String token) {
        String email = redisTemplate.opsForValue().get(EMAIL_PREFIX + token);
        return Optional.ofNullable(email);
    }

    public void deleteVerificationToken(String token) {
        redisTemplate.delete(EMAIL_PREFIX + token);
    }

    public void sendVerificationCode(String email) {
        String code = generateVerificationCode();
        redisTemplate.opsForValue().set(
                EMAIL_PREFIX + email,
                code,
                EXPIRATION_TIME,
                TimeUnit.MINUTES
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Qrystal 회원가입 인증코드");
        message.setText("인증 코드: " + code + "\n" + EXPIRATION_TIME + "분 이내에 입력해주세요.");

        emailSender.send(message);
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(EMAIL_PREFIX + email);
        if (storedCode == null) {
            throw new CustomException(ErrorCode.VERIFICATION_EXPIRED);
        }

        boolean isValid = storedCode.equals(code);
        if (isValid) {
            redisTemplate.delete(EMAIL_PREFIX + email);
        }
        return isValid;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}