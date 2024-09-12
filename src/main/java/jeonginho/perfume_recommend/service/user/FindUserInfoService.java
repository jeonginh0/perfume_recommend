package jeonginho.perfume_recommend.service.user;

import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindUserInfoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private int smtpTimeout;

    // 이메일 찾기 메서드
    public String findEmailByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NoSuchElementException("해당 휴대폰 번호로 등록된 사용자가 없습니다."));
        return user.getEmail();
    }

    // 비밀번호 찾기 메서드
    public void resetPassword(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("해당 이메일의 사용자를 찾을 수 없습니다."));

            // 랜덤 비밀번호 생성
            String newPassword = generateRandomPassword();

            // 새 비밀번호를 암호화하여 저장
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // 이메일로 새 비밀번호 전송
            sendPasswordResetEmail(user.getEmail(), newPassword);

        } catch (NoSuchElementException e) {
            System.err.println("사용자를 찾을 수 없습니다: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.err.println("비밀번호 재설정 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 랜덤 비밀번호 생성 메서드
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    // 비밀번호 재설정 이메일 전송 메서드
    private void sendPasswordResetEmail(String email, String newPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("비밀번호 재설정 안내");
            message.setText("새로운 비밀번호는 다음과 같습니다: " + newPassword + "\n로그인 후 비밀번호를 변경하는 것을 권장드립니다.");

            mailSender.send(message);

            System.out.println("메일 서버 호스트: " + mailHost);
            System.out.println("메일 서버 포트: " + mailPort);
            System.out.println("SMTP 타임아웃: " + smtpTimeout + "ms");

        } catch (Exception e) {
            System.err.println("이메일 전송 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
