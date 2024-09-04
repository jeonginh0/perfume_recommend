package jeonginho.perfume_recommend.controller.user;

import jeonginho.perfume_recommend.dto.user.preference.UserPreferencesDto;
import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 사용자 ID로 닉네임 조회
    @GetMapping("/nickname/{id}")
    public ResponseEntity<String> getNicknameById(@PathVariable String id) {
        try {
            String nickname = userService.getNicknameById(id);
            return ResponseEntity.ok(nickname);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
        }
    }

    // 사용자 계정 생성
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.create(
                user.getNickname(),
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNumber()
        );

        System.out.println("유저 정보 : " + savedUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // 모든 사용자 조회
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // 로컬 로그인
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        try {
            String token = userService.login(user.getEmail(), user.getPassword());
            System.out.println("로그인 성공 ! " + user.getEmail() + user.getPassword());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    // 로컬 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        System.out.println("로그아웃 성공 ! ");
        return ResponseEntity.ok("로그아웃이 성공적으로 처리되었습니다.");
    }

    // 로컬/소셜 로그인 시 설문 컨트롤러
    @PostMapping("/update-preferences")
    public ResponseEntity<?> updateUserPreferences(@RequestParam String email,
                                                   @RequestBody UserPreferencesDto preferencesDto,
                                                   @RequestHeader("Authorization") String token) {

        // Authorization 헤더에서 "Bearer " 제거
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        User updatedUser = userService.updateUserPreferences(email, preferencesDto, jwtToken);
        return ResponseEntity.ok(updatedUser);
    }


}
