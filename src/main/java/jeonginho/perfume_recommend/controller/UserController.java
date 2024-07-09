package jeonginho.perfume_recommend.controller;

import jeonginho.perfume_recommend.dto.user.CreateUserRequestDto;
import jeonginho.perfume_recommend.dto.user.UserResponseDto;
import jeonginho.perfume_recommend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
*
* UserController.java (class)
*
* 1. createUser : 새로운 사용자를 생성하는 엔드포인트
* 2. getUser : 이메일을 통해 사용자를 조회하는 엔드포인트
*
* */

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody CreateUserRequestDto dto) {
        UserResponseDto responseDto = userService.createUser(dto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String email) {
        UserResponseDto dto = userService.getUserByEmail(email);
        return ResponseEntity.ok(dto);
    }
}
