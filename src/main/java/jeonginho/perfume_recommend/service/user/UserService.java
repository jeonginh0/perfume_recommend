package jeonginho.perfume_recommend.service.user;

import com.fasterxml.jackson.databind.JsonNode;
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
                       String password, String phoneNumber) {
        User user = new User();
        user.setNickname(nickname); //닉네임 기입
        user.setEmail(email); //이메일 기입
        user.setPassword(passwordEncoder.encode(password)); //비밀번호 기입
        user.setPhoneNumber(phoneNumber); //휴대폰 정보 기입
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

    public String getUserNicknameById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        return user.getNickname(); // 사용자 닉네임 필드가 있다고 가정
    }

    public String login(String email, String password) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtUtil.generateToken(user.getId());
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

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setNickname(name);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setCreatedAt(LocalDateTime.now());

            return userRepository.save(newUser);
        }
    }

    public User createOrGetUser(String email, String name, String phoneNumber) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setNickname(name);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setCreatedAt(LocalDateTime.now());

            return userRepository.save(newUser);
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
                newUser.setCreatedAt(LocalDateTime.now());

                return userRepository.save(newUser);
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error response status: " + e.getStatusCode());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get user info from Kakao", e);
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

    // 네이버 사용자 정보 요청
    public User createOrGetNaverUser(String accessToken) {
        String url = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<NaverUserResponse> response = restTemplate.exchange(url, HttpMethod.GET, request, NaverUserResponse.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to get user info from Naver");
            }

            NaverUserResponse.Response userInfo = response.getBody().getResponse();

            String email = userInfo.getEmail();
            String nickname = userInfo.getNickname();
            String phoneNumber = userInfo.getMobile();


            if (email == null) {
                throw new RuntimeException("Email not provided by Naver");
            }

            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                return existingUser.get();
            } else {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setNickname(nickname);
                newUser.setPhoneNumber(phoneNumber);
                newUser.setCreatedAt(LocalDateTime.now());

                return userRepository.save(newUser);
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error response status: " + e.getStatusCode());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get user info from Naver", e);
        }
    }

    //google 로그인 창 url
    public String getGoogleLoginUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + env.getProperty("spring.security.oauth2.client.registration.google.client-id")
                + "&redirect_uri=http://localhost:8080/api/v1/oauth2/google&response_type=code&scope=email%20profile%20openid&access_type=offline";
    }

    //kakao 로그인 창 url
    public String getKakaoLoginUrl() {
        return "https://kauth.kakao.com/oauth/authorize?client_id=" + env.getProperty("kakao.client-id")
                + "&redirect_uri=" + env.getProperty("kakao.redirect-uri") + "&response_type=code";
    }

    //naver 로그인 창 url
    public String getNaverLoginUrl(String state) {
        return "https://nid.naver.com/oauth2.0/authorize?client_id=" + env.getProperty("naver.client-id")
                + "&response_type=code&redirect_uri=" + env.getProperty("naver.redirect-uri")
                + "&state=" + state;
    }
}
