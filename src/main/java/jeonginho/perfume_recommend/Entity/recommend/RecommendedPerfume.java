package jeonginho.perfume_recommend.Entity.recommend;

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
@Document(collection = "recommended_perfumes")
public class RecommendedPerfume {
    @Id
    private String id;

    private String userId; // 사용자 ID (회원일 경우)
    private List<PerfumeRecommendation> perfumeRecommendations; // 향수 ID와 추천 이유 목록

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PerfumeRecommendation {
        private String perfumeId; // 추천된 향수의 ID
        private String recommendationReason; // 추천 이유
    }
}
