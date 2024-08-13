package jeonginho.perfume_recommend.dto.user.kakao;

import lombok.Data;

import java.util.List;

@Data
public class KakaoUserResponse {
    private Long id;
    private KakaoAccount kakao_account;

    @Data
    public static class KakaoAccount {
        private String email;
        private KakaoProfile profile;
        private String phone_number;

        private List<String> preferenceNote;
        private List<String> preferenceDuration;
        private List<String> preferenceSeason;
        private List<String> preferenceSituation;
        @Data
        public static class KakaoProfile {
            private String nickname;
        }
    }
}
