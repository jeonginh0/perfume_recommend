package jeonginho.perfume_recommend.service.user;

import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import jeonginho.perfume_recommend.dto.user.google.GoogleInfResponse;
import jeonginho.perfume_recommend.dto.user.google.GoogleRequest;
import jeonginho.perfume_recommend.dto.user.google.GoogleResponse;
import jeonginho.perfume_recommend.dto.user.kakao.KakaoUserResponse;
import jeonginho.perfume_recommend.dto.user.naver.NaverRequest;
import jeonginho.perfume_recommend.dto.user.naver.NaverResponse;
import jeonginho.perfume_recommend.dto.user.naver.NaverUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

@Service
public class UserSocialLoginService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${naver.client-id}")
    private String naverClientId;

    @Value("${naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${naver.client-secret}")
    private String naverClientSecret;

    // Google OAuth 처리
    public String handleGoogleLogin(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        // 구글 토큰을 받아오기 위한 요청
        GoogleResponse googleResponse = restTemplate.postForObject(
                "https://oauth2.googleapis.com/token",
                buildGoogleTokenRequest(authCode),
                GoogleResponse.class
        );

        // 구글 유저 정보를 가져옴
        GoogleInfResponse googleInfResponse = restTemplate.getForObject(
                "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + googleResponse.getAccess_token(),
                GoogleInfResponse.class
        );

        // User 저장 혹은 기존 유저 반환
        Optional<User> user = userService.createOrGetGoogleUser(googleInfResponse);

        // Optional 값 검증
        return user.map(u -> jwtTokenProvider.createToken(u.getEmail(), u.getId()))
                .orElseThrow(() -> new RuntimeException("유저 정보를 저장하지 못했습니다."));
    }

    // Kakao OAuth 처리
    public String handleKakaoLogin(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        // 카카오 토큰을 받아오기 위한 요청
        KakaoUserResponse kakaoUser = restTemplate.postForObject(
                "https://kauth.kakao.com/oauth/token",
                buildKakaoTokenRequest(authCode),
                KakaoUserResponse.class
        );

        // 유저 저장 혹은 기존 유저 반환
        User user = userService.createOrGetKakaoUser(kakaoUser);

        // JWT 생성 후 반환
        return jwtTokenProvider.createToken(user.getEmail(), user.getId());
    }

    // Naver OAuth 처리
    public String handleNaverLogin(String authCode, String state) {
        RestTemplate restTemplate = new RestTemplate();

        // 네이버 토큰을 받아오기 위한 요청
        NaverResponse naverResponse = restTemplate.postForObject(
                "https://nid.naver.com/oauth2.0/token",
                buildNaverTokenRequest(authCode, state),
                NaverResponse.class
        );

        // 네이버 유저 정보를 가져옴
        NaverUserResponse naverUser = restTemplate.getForObject(
                "https://openapi.naver.com/v1/nid/me?access_token=" + naverResponse.getAccess_token(),
                NaverUserResponse.class
        );

        // 유저 저장 혹은 기존 유저 반환
        User user = userService.createOrGetNaverUser(naverUser);

        // JWT 생성 후 반환
        return jwtTokenProvider.createToken(user.getEmail(), user.getId());
    }

    //google 로그인 창 url
    public String getGoogleLoginUrl() {
        try {
            String encodedClientId = URLEncoder.encode(googleClientId, "UTF-8");
            return "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + encodedClientId
                    + "&redirect_uri=http://localhost:8080/api/v1/oauth2/google&response_type=code&scope=email%20profile%20openid&access_type=offline";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL 인코딩 오류 발생", e);
        }
    }

    //kakao 로그인 창 url
    public String getKakaoLoginUrl() {
        return "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId
                + "&redirect_uri=" + kakaoRedirectUri + "&response_type=code";
    }

    //naver 로그인 창 url
    public String getNaverLoginUrl(String state) {
        return "https://nid.naver.com/oauth2.0/authorize?client_id=" + naverClientId
                + "&response_type=code&redirect_uri=" + naverRedirectUri
                + "&state=" + state;
    }

    // Google Access Token 요청 메서드
    public GoogleRequest buildGoogleTokenRequest(String authCode) {
        return GoogleRequest
                .builder()
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .code(authCode)
                .redirectUri(googleRedirectUri)
                .grantType("authorization_code").build();
    }

    // Kakao Access Token 요청 메서드
    public String buildKakaoTokenRequest(String authCode) {
        return "https://kauth.kakao.com/oauth/token?"
                + "grant_type=authorization_code"
                + "&client_id=" + kakaoClientId
                + "&redirect_uri=" + kakaoRedirectUri
                + "&code=" + authCode;
    }

    // Naver Access Token 요청 메서드
    public NaverRequest buildNaverTokenRequest(String authCode, String state) {
        return NaverRequest.builder()
                .clientId(naverClientId)
                .clientSecret(naverClientSecret)
                .code(authCode)
                .grantType("authorization_code")
                .build();
    }

}
