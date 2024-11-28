package jeonginho.perfume_recommend.controller.user;

import jeonginho.perfume_recommend.dto.ErrorDto;
import jeonginho.perfume_recommend.dto.ResponseDto;
import jeonginho.perfume_recommend.service.user.UserSocialLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/oauth2")
public class UserSocialLoginController {

    @Autowired
    private UserSocialLoginService userSocialLoginService;

    // Google 로그인 처리
    @GetMapping("/google")
    public ResponseEntity<String> googleLogin(@RequestParam("code") String authCode) {
        try {
            String token = userSocialLoginService.handleGoogleLogin(authCode);
            String redirectUrl = "http://localhost:3000/login?token=" + token;
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUrl).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: " + e.getMessage());
        }
    }

    // Kakao 로그인 처리
    @GetMapping("/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestParam("code") String authCode) {
        try {
            String token = userSocialLoginService.handleKakaoLogin(authCode);
            String redirectUrl = "http://localhost:3000/login?token=" + token;
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUrl).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: " + e.getMessage());
        }
    }

    // Naver 로그인 처리
    @GetMapping("/naver")
    public ResponseEntity<String> naverLogin(@RequestParam("code") String authCode) {
        try {
            String token = userSocialLoginService.handleNaverLogin(authCode);
            String redirectUrl = "http://localhost:3000/login?token=" + token;
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUrl).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: " + e.getMessage());
        }
    }

    // Google 로그인 URL 얻기
    @GetMapping("/google/url")
    public String getGoogleLoginUrl() {
        return userSocialLoginService.getGoogleLoginUrl();
    }

    // Kakao 로그인 URL 얻기
    @GetMapping("/kakao/url")
    public String getKakaoLoginUrl() {
        return userSocialLoginService.getKakaoLoginUrl();
    }

    // Naver 로그인 URL 얻기
    @GetMapping("/naver/url")
    public String getNaverLoginUrl() {
        String state = UUID.randomUUID().toString();
        return userSocialLoginService.getNaverLoginUrl(state);
    }
}
