package jeonginho.perfume_recommend.dto.user.naver;

import lombok.Data;

@Data
public class NaverResponse {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;
    private String error;
    private String error_description;
}
