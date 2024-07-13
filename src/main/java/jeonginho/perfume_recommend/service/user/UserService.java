package jeonginho.perfume_recommend.service.user;

import jeonginho.perfume_recommend.model.User;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import jeonginho.perfume_recommend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User create(String nickname, String email, String password, String phoneNumber, String preferenceAcode, String preferenceSeason) {
        User user = new User();
        user.setNickname(nickname); //닉네임 기입
        user.setEmail(email); //이메일 기입
        user.setPassword(passwordEncoder.encode(password)); //비밀번호 기입
        user.setPhoneNumber(phoneNumber); //휴대폰 정보 기입
        user.setPreferenceAcode(preferenceAcode);
        user.setPreferenceSeason(preferenceSeason);
        user.setCreatedAt(LocalDateTime.now());
        this.userRepository.save(user);
        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public String login(String email, String password) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtUtil.generateToken(user.getEmail());
            } else {
                throw new Exception("Invalid credentials");
            }
        } else {
            throw new Exception("User not found");
        }
    }
}
