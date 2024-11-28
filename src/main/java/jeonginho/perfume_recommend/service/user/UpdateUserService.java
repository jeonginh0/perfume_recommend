package jeonginho.perfume_recommend.service.user;

import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UpdateUserService {

    private final UserRepository userRepository;

    // 낙네임 변경
    public User updateUserNickname(String id, String newNickName) {
        // 동일한 닉네임이 존재하는지 확인
        if (userRepository.existsByNickname(newNickName)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        user.setNickname(newNickName);

        return userRepository.save(user);
    }

    // 비밀번호 변경
    public User updateUserPassword(String id, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        user.setPassword(newPassword);

        return userRepository.save(user);
    }

}
