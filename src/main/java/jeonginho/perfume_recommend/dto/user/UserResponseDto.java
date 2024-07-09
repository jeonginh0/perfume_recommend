package jeonginho.perfume_recommend.dto.user;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDto {
    private String id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}
