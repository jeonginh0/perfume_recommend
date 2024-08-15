package jeonginho.perfume_recommend.controller.user;

import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.service.user.UserService;
import jeonginho.perfume_recommend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserSocialLoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientKey;

    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${naver.client-id}")
    private String naverClientId;
    @Value("${naver.client-secret}")
    private String naverClientSecret;
    @Value("${naver.redirect-uri}")
    private String naverRedirectUri;

    /*
     * GOOGLE 소셜 로그인
     * */
    @PostMapping(value="api/v1/oauth2/google")
    public String loginUrlGoogle() {
        return userService.getGoogleLoginUrl();
    }

    @GetMapping(value="api/v1/oauth2/google")
    public String loginGoogle(@RequestParam(value = "code") String authCode){
        String jwtToken = userService.getGoogleAccessToken(authCode);
        User user = userService.createOrGetGoogleUser(jwtToken);

        String token = jwtUtil.generateToken(user.getEmail());
        return token;
    }

    /*
     * KAKAO 소셜 로그인
     * */
    @PostMapping(value="api/v1/oauth2/kakao")
    public String loginUrlKakao() {
        return userService.getKakaoLoginUrl();
    }

    @GetMapping(value="api/v1/oauth2/kakao")
    public String loginKakao(@RequestParam(value = "code") String authcode) {
        String accessToken = userService.getKakaoAccessToken(authcode);
        User user = userService.createOrGetKakaoUser(accessToken);

        String jwtToken = jwtUtil.generateToken(user.getEmail());

        return jwtToken;
    }

    /*
    * NAVER 소셜 로그인
    * */
    @PostMapping(value="api/v1/oauth2/naver")
    public String loginUrlNaver() {
        String state = UUID.randomUUID().toString();
        return userService.getNaverLoginUrl(state);
    }

    @GetMapping(value="api/v1/oauth2/naver")
    public String loginNaver(@RequestParam String code, String state) {
        String accessToken = userService.getNaverAccessToken(code, state);
        User user = userService.createOrGetNaverUser(accessToken);

        String jwtToken = jwtUtil.generateToken(user.getEmail());

        return jwtToken;
    }
}
