package jeonginho.perfume_recommend.Entity.survey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "survey_responses")
public class SurveyResponse {
    @Id
    private String id; // MongoDB에서 자동 생성되는 고유 ID

    private String userId; // 회원일 경우 사용자 ID
    private String guestSessionId; // 비회원일 경우 세션 ID

    private List<ResponseItem> responses; // 설문 응답 리스트

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponseItem {
        private String questionType;  // 질문 유형 ("category", "season", "situation", "duration")
        private String response;  // 사용자 응답 ("Floral", "Spring", 등)
        private Integer weight; // 해당 질문의 가중치
    }
}
