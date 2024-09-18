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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
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

        try {
            // 파라미터를 MultiValueMap으로 구성
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", kakaoClientId);
            params.add("redirect_uri", kakaoRedirectUri);
            params.add("code", authCode);

            // 카카오 토큰 요청
            KakaoUserResponse kakaoUser = restTemplate.postForObject(
                    "https://kauth.kakao.com/oauth/token",
                    params,
                    KakaoUserResponse.class
            );

            // 카카오 사용자 정보 요청
            KakaoUserResponse kakaoUserInfo = restTemplate.getForObject(
                    "https://kapi.kakao.com/v2/user/me?access_token=" + kakaoUser.getAccess_token(),
                    KakaoUserResponse.class
            );

            // 유저 저장 혹은 기존 유저 반환
            User user = userService.createOrGetKakaoUser(kakaoUserInfo);

            // JWT 생성 후 반환
            return jwtTokenProvider.createToken(user.getEmail(), user.getId());

        } catch (RestClientException e) {
            System.out.println("카카오 로그인 중 오류 발생: "+ e.getMessage());
            throw new RuntimeException("카카오 로그인 처리 중 오류가 발생했습니다.");
        }
    }

    // Naver OAuth 처리
    public String handleNaverLogin(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 쿼리 문자열로 변환된 요청 본문
            String body = buildNaverTokenRequest(authCode);
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            // 네이버 토큰 요청
            NaverResponse naverResponse = restTemplate.postForObject(
                    "https://nid.naver.com/oauth2.0/token",
                    request,
                    NaverResponse.class
            );

            if (naverResponse == null || naverResponse.getAccess_token() == null) {
                throw new RuntimeException("네이버 응답에 액세스 토큰이 없습니다.");
            }

            // 네이버 유저 정보를 가져옴
            NaverUserResponse naverUser = restTemplate.getForObject(
                    "https://openapi.naver.com/v1/nid/me?access_token=" + naverResponse.getAccess_token(),
                    NaverUserResponse.class
            );

            if (naverUser == null) {
                throw new RuntimeException("네이버 유저 정보를 가져오는 데 실패했습니다.");
            }

            // 유저 저장 혹은 기존 유저 반환
            User user = userService.createOrGetNaverUser(naverUser);

            // JWT 생성 후 반환
            return jwtTokenProvider.createToken(user.getEmail(), user.getId());

        } catch (RestClientException e) {
            System.out.println("네이버 로그인 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("네이버 로그인 처리 중 오류가 발생했습니다.");
        }
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
        try{
            String encodedClientId = URLEncoder.encode(kakaoClientId, "UTF-8");
            String encodedRedirectUri = URLEncoder.encode(kakaoRedirectUri, "UTF-8");
            return "https://kauth.kakao.com/oauth/authorize?client_id=" + encodedClientId
                    + "&redirect_uri=" + encodedRedirectUri + "&response_type=code";
        }catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL 인코딩 오류 발생", e);
        }
    }

    //naver 로그인 창 url
    public String getNaverLoginUrl(String state) {
        try {
            String encodedClientId = URLEncoder.encode(naverClientId, "UTF-8");
            String encodedRedirectUri = URLEncoder.encode(naverRedirectUri, "UTF-8");
            return "https://nid.naver.com/oauth2.0/authorize?client_id=" + encodedClientId
                    + "&response_type=code&redirect_uri=" + encodedRedirectUri
                    + "&state=" + state;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL 인코딩 오류 발생", e);
        }
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
    public String buildNaverTokenRequest(String authCode) {
        return "client_id=" + naverClientId
                + "&client_secret=" + naverClientSecret
                + "&code=" + authCode
                + "&grant_type=authorization_code";
    }

}
