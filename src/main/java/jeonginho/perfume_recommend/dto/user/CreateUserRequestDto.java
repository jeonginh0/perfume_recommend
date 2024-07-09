package jeonginho.perfume_recommend.dto.user;

import lombok.Data;

@Data
public class CreateUserRequestDto {
    private String name;
    private String email;
    private String password;
}
