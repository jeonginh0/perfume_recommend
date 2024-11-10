package jeonginho.perfume_recommend.dto.embedding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // 정의되지 않은 필드를 무시
public class EmbeddingResponse {
    private List<EmbeddingData> Data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmbeddingData {
        private List<Double> embedding;
    }
}