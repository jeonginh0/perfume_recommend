package jeonginho.perfume_recommend.service.user;

import com.fasterxml.jackson.databind.JsonNode;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import jeonginho.perfume_recommend.dto.user.google.GoogleInfResponse;
import jeonginho.perfume_recommend.dto.user.google.GoogleRequest;
import jeonginho.perfume_recommend.dto.user.google.GoogleResponse;
import jeonginho.perfume_recommend.dto.user.kakao.KakaoUserResponse;
import jeonginho.perfume_recommend.dto.user.naver.NaverResponse;
import jeonginho.perfume_recommend.dto.user.naver.NaverUserResponse;
import jeonginho.perfume_recommend.dto.user.preference.UserPreferencesDto;
import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import jeonginho.perfume_recommend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final RestTemplate restTemplate;

    // 사용자 ID로 닉네임 조회
    public String getNicknameById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        return user.getNickname();
    }

    public User create(String nickname, String email,
                       String password, String phoneNumber) {
        User user = new User();
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        user.setCreatedAt(LocalDateTime.now());
        this.userRepository.save(user);
        return user;
    }

    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public String getEmailFromToken(String token) {
        return jwtUtil.extractEmail(token);
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

    public User updateUserPreferences(String email, UserPreferencesDto preferencesDto, String token) {
        String tokenEmail = jwtUtil.extractEmail(token);

        if (!email.equals(tokenEmail)) {
            throw new SecurityException("Invalid token for the provided email");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return userRepository.save(user);
    }

    /*
     * GOOGLE LOGIN
     * */
    // 구글 액세스 토큰 가져오기
    public String getGoogleAccessToken(String authCode) {
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

    // 구글 사용자 정보 가져오기
    public GoogleInfResponse getGoogleUserInfo(String accessToken) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + accessToken;

        ResponseEntity<GoogleInfResponse> response = restTemplate.getForEntity(url, GoogleInfResponse.class);
        return response.getBody();
    }

    public Optional<User> createOrGetGoogleUser(GoogleInfResponse googleInfResponse) {
        String email = googleInfResponse.getEmail();
        String name = googleInfResponse.getName();
        String phoneNumber = googleInfResponse.getPhoneNumber(); // 구글에서 전화번호를 받아올 수 있는 경우

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser;
        } else {
            User newUser = User.builder()
                    .email(email)
                    .nickname(name)
                    .phoneNumber(phoneNumber)
                    .createdAt(LocalDateTime.now()) // 생성일자 설정
                    .build();
            userRepository.save(newUser);
            return Optional.of(newUser);
        }
    }

    /*
     * KAKAO LOGIN
     * */
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
            System.out.println("Error response status: " + e.getStatusCode());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get Kakao access token", e);
        }
    }

    public User createOrGetKakaoUser(KakaoUserResponse kakaoUserResponse) {
        String email = kakaoUserResponse.getKakao_account().getEmail();
        String nickname = kakaoUserResponse.getKakao_account().getProfile().getNickname();

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .createdAt(LocalDateTime.now()) // 생성일자 설정
                    .build();
            userRepository.save(newUser);
            return newUser;
        }
    }

    /*
     * NAVER LOGIN
     * */
    // 네이버 로그인 Access Token 요청
    public String getNaverAccessToken(String code, String state) {
        String url = "https://nid.naver.com/oauth2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", env.getProperty("naver.client-id"));
        params.add("client_secret", env.getProperty("naver.client-secret"));
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<NaverResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, NaverResponse.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody().getAccess_token();
            } else {
                throw new RuntimeException("Failed to get Naver access token: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error response status: " + e.getStatusCode());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get Naver access token", e);
        }
    }

    public User createOrGetNaverUser(NaverUserResponse naverUserResponse) {
        String email = naverUserResponse.getResponse().getEmail();
        String nickname = naverUserResponse.getResponse().getNickname(); // 또는 naverUserResponse에서 직접 가져오는 필드명 사용

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .createdAt(LocalDateTime.now()) // 생성일자 설정
                    .build();
            userRepository.save(newUser);
            return newUser;
        }
    }
}
