package jeonginho.perfume_recommend.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import jeonginho.perfume_recommend.dto.user.google.GoogleInfResponse;
import jeonginho.perfume_recommend.dto.user.google.GoogleRequest;
import jeonginho.perfume_recommend.dto.user.google.GoogleResponse;
import jeonginho.perfume_recommend.dto.user.kakao.KakaoUserResponse;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

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
                       List<String> preferenceNote, List<String> preferenceDuration,
                       List<String> preferenceSeason, List<String> preferenceSituation) {
        User user = new User();
        user.setNickname(nickname); //닉네임 기입
        user.setEmail(email); //이메일 기입
        user.setPassword(passwordEncoder.encode(password)); //비밀번호 기입
        user.setPhoneNumber(phoneNumber); //휴대폰 정보 기입
        user.setPreferenceNote(preferenceNote);
        user.setPreferenceDuration(preferenceDuration);
        user.setPreferenceSeason(preferenceSeason);
        user.setPreferenceSituation(preferenceSituation);
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

    // 구글 액세스 토큰 가져오기
    public String getGoogleAccessToken(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        GoogleRequest googleOAuthRequestParam = GoogleRequest
                .builder()
                .clientId(env.getProperty("spring.security.oauth2.client.registration.google.client-id"))
                .clientSecret(env.getProperty("spring.security.oauth2.client.registration.google.client-secret"))
                .code(authCode)
                .redirectUri("http://localhost:8080/api/v1/oauth2/google")
                .grantType("authorization_code").build();

        ResponseEntity<GoogleResponse> resultEntity = restTemplate.postForEntity("https://oauth2.googleapis.com/token",
                googleOAuthRequestParam, GoogleResponse.class);

        return resultEntity.getBody().getId_token();
    }

    // 구글 사용자 정보 가져오기 및 사용자 생성 또는 가져오기
    public User createOrGetGoogleUser(String jwtToken) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> map = new HashMap<>();
        map.put("id_token", jwtToken);

        ResponseEntity<GoogleInfResponse> resultEntity2 = restTemplate.postForEntity("https://oauth2.googleapis.com/tokeninfo",
                map, GoogleInfResponse.class);

        String email = resultEntity2.getBody().getEmail();
        String name = resultEntity2.getBody().getName();
        String phoneNumber = resultEntity2.getBody().getPhoneNumber(); // 추가 정보
        List<String> preferenceNote = resultEntity2.getBody().getPreferenceNote(); // 추가 정보
        List<String> preferenceDuration = resultEntity2.getBody().getPreferenceDuration(); // 추가 정보
        List<String> preferenceSeason = resultEntity2.getBody().getPreferenceSeason(); // 추가 정보
        List<String> preferenceSituation = resultEntity2.getBody().getPreferenceSituation(); // 추가 정보

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setNickname(name);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setPreferenceNote(preferenceNote);
            newUser.setPreferenceDuration(preferenceDuration);
            newUser.setPreferenceSeason(preferenceSeason);
            newUser.setPreferenceSituation(preferenceSituation);
            newUser.setCreatedAt(LocalDateTime.now());

            return userRepository.save(newUser);
        }
    }

    public User createOrGetUser(String email, String name, String phoneNumber,
                                List<String> preferenceNote, List<String> preferenceDuration, List<String> preferenceSeason, List<String> preferenceSituation) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setNickname(name);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setPreferenceNote(preferenceNote);
            newUser.setPreferenceDuration(preferenceDuration);
            newUser.setPreferenceSeason(preferenceSeason);
            newUser.setPreferenceSituation(preferenceSituation);
            newUser.setCreatedAt(LocalDateTime.now());

            return userRepository.save(newUser);
        }
    }

    // 카카오 로그인 Access Token 요청
    public String getKakaoAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", env.getProperty("kakao.client-id"));
        params.add("redirect_uri", env.getProperty("kakao.redirect-uri"));
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, request, JsonNode.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody().path("access_token").asText();
            } else {
                throw new RuntimeException("Failed to get Kakao access token: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            // 응답 상태 코드와 본문을 로깅하여 문제를 진단
            System.out.println("Error response status: " + e.getStatusCode());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get Kakao access token", e);
        }
    }

    // 카카오 사용자 정보 요청
    public User createOrGetKakaoUser(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(url, HttpMethod.GET, request, KakaoUserResponse.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to get user info from Kakao");
            }

            KakaoUserResponse userInfo = response.getBody();

            String email = userInfo.getKakao_account().getEmail();
            String nickname = userInfo.getKakao_account().getProfile().getNickname();
            String phoneNumber = userInfo.getKakao_account().getPhone_number();
            List<String> preferenceNote = userInfo.getKakao_account().getPreferenceNote();
            List<String> preferenceDuration = userInfo.getKakao_account().getPreferenceDuration();
            List<String> preferenceSeason = userInfo.getKakao_account().getPreferenceSeason();
            List<String> preferenceSituation = userInfo.getKakao_account().getPreferenceSituation();

            if (email == null) {
                throw new RuntimeException("Email not provided by Kakao");
            }

            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                return existingUser.get();
            } else {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setNickname(nickname);
                newUser.setPhoneNumber(phoneNumber);
                newUser.setPreferenceNote(preferenceNote);
                newUser.setPreferenceDuration(preferenceDuration);
                newUser.setPreferenceSeason(preferenceSeason);
                newUser.setPreferenceSituation(preferenceSituation);
                newUser.setCreatedAt(LocalDateTime.now());

                return userRepository.save(newUser);
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error response status: " + e.getStatusCode());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get user info from Kakao", e);
        }
    }

    public String getGoogleLoginUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + env.getProperty("spring.security.oauth2.client.registration.google.client-id")
                + "&redirect_uri=http://localhost:8080/api/v1/oauth2/google&response_type=code&scope=email%20profile%20openid&access_type=offline";
    }

    public String getKakaoLoginUrl() {
        return "https://kauth.kakao.com/oauth/authorize?client_id=" + env.getProperty("kakao.client-id")
                + "&redirect_uri=" + env.getProperty("kakao.redirect-uri") + "&response_type=code";
    }
}
