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
@RequestMapping("/api/v1/oauth2")
public class UserSocialLoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /*
     * GOOGLE 소셜 로그인
     * */
    @PostMapping("/google")
    public void loginUrlGoogle(HttpServletResponse response) throws IOException {
        String googleLoginUrl = userService.getGoogleLoginUrl();
        System.out.println(googleLoginUrl);
        response.sendRedirect(googleLoginUrl);
    }

    @GetMapping("/google")
    public ResponseEntity<String> loginGoogle(@RequestParam(value = "code") String authCode) {
        try {
            String jwtToken = userService.getGoogleAccessToken(authCode);
            User user = userService.createOrGetGoogleUser(jwtToken);

            String token = jwtTokenProvider.createToken(user.getEmail(), user.getId());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /*
     * KAKAO 소셜 로그인
     * */
    @PostMapping("/kakao")
    public void loginUrlKakao(HttpServletResponse response) throws IOException {
        String kakaoLoginUrl = userService.getKakaoLoginUrl();
        System.out.println(kakaoLoginUrl);
        response.sendRedirect(kakaoLoginUrl);
    }

    @GetMapping("/kakao")
    public ResponseEntity<String> loginKakao(@RequestParam(value = "code") String authCode) {
        try {
            String accessToken = userService.getKakaoAccessToken(authCode);
            User user = userService.createOrGetKakaoUser(accessToken);

            String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /*
     * NAVER 소셜 로그인
     * */
    @PostMapping("/naver")
    public void loginUrlNaver(HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();
        String naverLoginUrl = userService.getNaverLoginUrl(state);
        System.out.println(naverLoginUrl);
        response.sendRedirect(naverLoginUrl);
    }

    @GetMapping("/naver")
    public ResponseEntity<String> loginNaver(@RequestParam String code, @RequestParam String state) {
        try {
            String accessToken = userService.getNaverAccessToken(code, state);
            User user = userService.createOrGetNaverUser(accessToken);

            String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
