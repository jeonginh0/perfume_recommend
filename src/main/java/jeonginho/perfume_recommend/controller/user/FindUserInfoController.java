package jeonginho.perfume_recommend.controller.user;

import jeonginho.perfume_recommend.service.user.FindUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/users")
public class FindUserInfoController {

    @Autowired
    FindUserInfoService findUserInfoService;

    // 비밀번호 찾기 API
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            findUserInfoService.resetPassword(email);
            return ResponseEntity.ok("새 비밀번호가 이메일로 전송되었습니다.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 재설정 중 오류가 발생했습니다.");
        }
    }

    // 이메일 찾기 API
    @PostMapping("/find-email")
    public ResponseEntity<String> findEmailByPhoneNumber(@RequestParam String phoneNumber) {
        try {
            String email = findUserInfoService.findEmailByPhoneNumber(phoneNumber);
            System.out.println("해당 휴대폰 번호로 등록된 이메일:" + email);
            return ResponseEntity.ok("해당 휴대폰 번호로 등록된 이메일: " + email);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 휴대폰 번호로 등록된 사용자가 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 찾기 중 오류가 발생했습니다.");
        }
    }
}
