package jeonginho.perfume_recommend.Entity.embedding;

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
@Document(collection = "embeddings")
public class Embedding {
    @Id
    private String id;
    private String perfumeId;
    private List<Double> embedding;
}