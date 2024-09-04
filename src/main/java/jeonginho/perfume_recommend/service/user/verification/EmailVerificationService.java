package jeonginho.perfume_recommend.service.user.verification;

import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final ConcurrentHashMap<String, String> verificationCodes = new ConcurrentHashMap<>();

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private int smtpTimeout;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    // 이메일로 인증번호 전송
    public void sendVerificationCode(String email) {
        String verificationCode = generateVerificationCode();
        verificationCodes.put(email, verificationCode);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(fromEmail);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode);

        // 메일 전송
        mailSender.send(message);

        // 디버그: 현재 설정된 메일 서버 정보 출력
        System.out.println("메일 서버 호스트: " + mailHost);
        System.out.println("메일 서버 포트: " + mailPort);
        System.out.println("SMTP 인증 사용: " + smtpAuth);
        System.out.println("SMTP 타임아웃: " + smtpTimeout);
        System.out.println("STARTTLS 사용: " + starttlsEnable);
    }

    // 인증번호 확인
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }

    // 6자리 인증번호 생성
    private String generateVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }
}
