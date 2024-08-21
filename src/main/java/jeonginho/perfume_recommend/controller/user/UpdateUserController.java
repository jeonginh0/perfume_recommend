package jeonginho.perfume_recommend.controller.user;

import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.service.user.UpdateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/users")
public class UpdateUserController {

    @Autowired
    UpdateUserService updateUserService;

    // 닉네임 변경
    @PostMapping("/{id}/nickname")
    public ResponseEntity<User> updateNickname(@PathVariable String id, @RequestParam String newNickname) {
        User updateUser = updateUserService.updateUserNickname(id, newNickname);
        return ResponseEntity.ok(updateUser);
    }

    // 패스워드 변경
    @PostMapping("/{id}/password")
    public ResponseEntity<User> updatePassword(@PathVariable String id, @RequestParam String newPassword, @RequestParam String confirmPassword) {

        User updateUser = updateUserService.updateUserPassword(id, newPassword, confirmPassword);

        return ResponseEntity.ok(updateUser);
    }
}
