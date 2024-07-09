package jeonginho.perfume_recommend.dto.user;

import lombok.Data;

@Data
public class UpdateUserRequestDto {
    private String name;
    private String email;
}