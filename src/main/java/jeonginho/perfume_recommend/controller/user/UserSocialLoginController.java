package jeonginho.perfume_recommend.controller.user;

import jakarta.servlet.http.HttpServletResponse;
import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import jeonginho.perfume_recommend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
public class UserSocialLoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /*
     * GOOGLE 소셜 로그인
     * */
    @PostMapping("/api/v1/oauth2/google")
    public void loginUrlGoogle(HttpServletResponse response) throws IOException {
        String googleLoginUrl = userService.getGoogleLoginUrl();
        response.sendRedirect(googleLoginUrl);
    }

    @GetMapping("/api/v1/oauth2/google")
    public ResponseEntity<String> loginGoogle(@RequestParam(value = "code") String authCode) {
        try {
            String accessToken = userService.getGoogleAccessToken(authCode);
            User user = userService.createOrGetGoogleUser(accessToken);

            // JWT 생성
            String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());

            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /*
     * KAKAO 소셜 로그인
     * */
    @PostMapping("/api/v1/oauth2/kakao")
    public void loginUrlKakao(HttpServletResponse response) throws IOException {
        String kakaoLoginUrl = userService.getKakaoLoginUrl();
        response.sendRedirect(kakaoLoginUrl);
    }

    @GetMapping("/api/v1/oauth2/kakao")
    public ResponseEntity<String> loginKakao(@RequestParam(value = "code") String authCode) {
        try {
            String accessToken = userService.getKakaoAccessToken(authCode);
            User user = userService.createOrGetKakaoUser(accessToken);

            // JWT 생성
            String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());

            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /*
     * NAVER 소셜 로그인
     * */
    @PostMapping("/api/v1/oauth2/naver")
    public void loginUrlNaver(HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();
        String naverLoginUrl = userService.getNaverLoginUrl(state);
        response.sendRedirect(naverLoginUrl);
    }

    @GetMapping("/api/v1/oauth2/naver")
    public ResponseEntity<String> loginNaver(@RequestParam String code, @RequestParam String state) {
        try {
            String accessToken = userService.getNaverAccessToken(code, state);
            User user = userService.createOrGetNaverUser(accessToken);

            // JWT 생성
            String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());

            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
