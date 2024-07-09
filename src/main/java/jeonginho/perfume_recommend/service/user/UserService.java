package jeonginho.perfume_recommend.service.user;

import jeonginho.perfume_recommend.dto.user.CreateUserRequestDto;
import jeonginho.perfume_recommend.dto.user.UpdateUserRequestDto;
import jeonginho.perfume_recommend.dto.user.UserResponseDto;
import jeonginho.perfume_recommend.exception.user.UserNotFoundException;
import jeonginho.perfume_recommend.model.User;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
*
* UserService.java (class)
*
* 1. createUser 메서드 : 새로운 사용자를 생성하는 메서드
* 2. getUserByEmail 메서드 : 이메일을 통해 사용자를 조회하는 메서드
* 3. updateUser 메서드 : 사용자의 정보를 업데이트하는 메서드
*
* */

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto createUser(CreateUserRequestDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // 암호화 필요 함 (구현예정)

        User savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserResponseDto updateUser(String email, UpdateUserRequestDto dto) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        // 이름 업데이트
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            user.setName(dto.getName());
        }

        // 이메일 업데이트
        if (dto.getEmail() != null && !dto.getEmail().isEmpty() && !dto.getEmail().equals(email)) {
            // 새 이메일이 이미 존재하는지 확인
            if (userRepository.findByEmail(dto.getEmail()) != null) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(dto.getEmail());
        }

        User updatedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .id(updatedUser.getId())
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .createdAt(updatedUser.getCreatedAt())
                .build();
    }

}
