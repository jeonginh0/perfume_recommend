package jeonginho.perfume_recommend.dto.user.naver;

import lombok.Data;

import java.util.List;

@Data
public class NaverUserResponse {
    private String resultcode;
    private String message;
    private Response response;

    @Data
    public static class Response {
        private String id;
        private String email;
        private String nickname;
        private String profile_image;
        private String age;
        private String gender;
        private String name;
        private String birthday;
        private String birthyear;
        private String mobile;

        private List<String> preferenceNote;
        private List<String> preferenceDuration;
        private List<String> preferenceSeason;
        private List<String> preferenceSituation;
    }
}
