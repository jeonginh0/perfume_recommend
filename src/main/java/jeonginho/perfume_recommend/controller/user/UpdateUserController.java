package jeonginho.perfume_recommend.controller.user;

import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.service.user.UpdateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UpdateUserController {

    @Autowired
    private UpdateUserService updateUserService;

    // 닉네임 변경
    @PostMapping("/nickname")
    public ResponseEntity<User> updateNickname(@RequestHeader("Authorization") String token, @RequestParam String newNickname) {
        // "Bearer " 제거
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        User updatedUser = updateUserService.updateUserNickname(jwtToken, newNickname);
        return ResponseEntity.ok(updatedUser);
    }

    // 비밀번호 변경
    @PostMapping("/password")
    public ResponseEntity<User> updatePassword(@RequestHeader("Authorization") String token, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        User updatedUser = updateUserService.updateUserPassword(jwtToken, newPassword, confirmPassword);
        return ResponseEntity.ok(updatedUser);
    }

    // 휴대폰 번호 변경
    @PostMapping("/phone")
    public ResponseEntity<User> updatePhoneNumber(@RequestHeader("Authorization") String token, @RequestParam String newPhoneNumber) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        User updatedUser = updateUserService.updateUserPhoneNumber(jwtToken, newPhoneNumber);
        return ResponseEntity.ok(updatedUser);
    }
}
