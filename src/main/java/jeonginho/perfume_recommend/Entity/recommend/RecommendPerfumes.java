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
@Document(collection = "recommend_perfumes")
public class RecommendPerfumes {

    @Id
    private String id; // MongoDB에서 자동으로 생성되는 ID

    private String userId; // 추천을 받은 사용자 ID

    private String sessionId;  // 추천을 받은 비회원 사용자 ID
    private List<String> perfumeIds; // 추천된 향수들의 ID 목록

}
