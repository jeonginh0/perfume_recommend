package jeonginho.perfume_recommend.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import jeonginho.perfume_recommend.model.User;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import jeonginho.perfume_recommend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Environment env;
    private final RestTemplate restTemplate = new RestTemplate();


    public User create(String nickname, String email,
                       String password, String phoneNumber,
                       List<String> preferenceAcode, String preferenceDuration,
                       String preferenceSeason) {
        User user = new User();
        user.setNickname(nickname); //닉네임 기입
        user.setEmail(email); //이메일 기입
        user.setPassword(passwordEncoder.encode(password)); //비밀번호 기입
        user.setPhoneNumber(phoneNumber); //휴대폰 정보 기입
        user.setPreferenceAcode(preferenceAcode);
        user.setPreferenceDuration(preferenceDuration);
        user.setPreferenceSeason(preferenceSeason);
        user.setCreatedAt(LocalDateTime.now());
        this.userRepository.save(user);
        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null);
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

    // google
    public void saveUserFromGoogle(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(name);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public User createOrGetUser(String email, String name, String phoneNumber,
                                List<String> preferenceAcode, String preferenceDuration, String preferenceSeason) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setNickname(name);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setPreferenceAcode(preferenceAcode);
            newUser.setPreferenceDuration(preferenceDuration);
            newUser.setPreferenceSeason(preferenceSeason);
            newUser.setCreatedAt(LocalDateTime.now());

            return userRepository.save(newUser);
        }
    }
}
