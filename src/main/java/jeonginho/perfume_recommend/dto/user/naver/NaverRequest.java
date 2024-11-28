package jeonginho.perfume_recommend.dto.user.naver;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NaverRequest {
    private String clientId;
    private String clientSecret;
    private String code;
    private String state;
    private String grantType;
}
