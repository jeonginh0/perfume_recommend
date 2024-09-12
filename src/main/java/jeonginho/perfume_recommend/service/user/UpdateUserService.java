package jeonginho.perfume_recommend.service.user;

import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UpdateUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;  // JWT 토큰 관련 서비스 추가

    // 닉네임 변경
    public User updateUserNickname(String token, String newNickname) {
        // JWT 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(token);

        // 동일한 닉네임이 존재하는지 확인
        if (userRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        user.setNickname(newNickname);

        return userRepository.save(user);
    }

    // 비밀번호 변경
    public User updateUserPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(newPassword));

        return userRepository.save(user);
    }

    // 휴대폰 번호 변경
    public User updateUserPhoneNumber(String token, String newPhoneNumber) {
        String email = jwtTokenProvider.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        user.setPhoneNumber(newPhoneNumber);

        return userRepository.save(user);
    }
}
