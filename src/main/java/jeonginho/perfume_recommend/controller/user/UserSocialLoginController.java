package jeonginho.perfume_recommend.controller.user;

import jeonginho.perfume_recommend.dto.user.google.GoogleInfResponse;
import jeonginho.perfume_recommend.dto.user.google.GoogleRequest;
import jeonginho.perfume_recommend.dto.user.google.GoogleResponse;
import jeonginho.perfume_recommend.model.User;
import jeonginho.perfume_recommend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserSocialLoginController {
    @Autowired
    private UserService userService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientKey;

    @RequestMapping(value="api/v1/oauth2/google", method=RequestMethod.POST)
    public String loginUrlGoogle() {
        String reqUrl = "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + googleClientId
                + "&redirect_uri=http://localhost:8080/api/v1/oauth2/google&response_type=code&scope=email%20profile%20openid&access_type=offline";

        return reqUrl;
    }

    @RequestMapping(value="/api/v1/oauth2/google", method = RequestMethod.GET)
    public String loginGoogle(@RequestParam(value = "code") String authCode){
        RestTemplate restTemplate = new RestTemplate();

        GoogleRequest googleOAuthRequestParam = GoogleRequest
                .builder()
                .clientId(googleClientId)
                .clientSecret(googleClientKey)
                .code(authCode)
                .redirectUri("http://localhost:8080/api/v1/oauth2/google")
                .grantType("authorization_code").build();

        ResponseEntity<GoogleResponse> resultEntity = restTemplate.postForEntity("https://oauth2.googleapis.com/token",
                googleOAuthRequestParam, GoogleResponse.class);

        String jwtToken=resultEntity.getBody().getId_token();

        Map<String, String> map=new HashMap<>();

        map.put("id_token",jwtToken);

        ResponseEntity<GoogleInfResponse> resultEntity2 = restTemplate.postForEntity("https://oauth2.googleapis.com/tokeninfo",
                map, GoogleInfResponse.class);

        String email = resultEntity2.getBody().getEmail();
        String name = resultEntity2.getBody().getName();
        String phoneNumber = resultEntity2.getBody().getPhoneNumber(); // 추가 정보
        List<String> preferenceAcode = resultEntity2.getBody().getPreferenceAcode(); // 추가 정보
        String preferenceDuration = resultEntity2.getBody().getPreferenceDuration(); // 추가 정보
        String preferenceSeason = resultEntity2.getBody().getPreferenceSeason(); // 추가 정보

        User existingUser = userService.getUserByEmail(email);

        if (existingUser != null) {
            System.out.println("이미 등록된 사용자 : " + existingUser.getNickname());
            return "이미 등록된 사용자: " + existingUser.getNickname();
        } else {
            userService.createOrGetUser(email, name, phoneNumber, preferenceAcode, preferenceDuration, preferenceSeason);
            System.out.println("새로 등록된 사용자 : " + name);
            return "새로운 사용자: " + name;
        }
    }
}
