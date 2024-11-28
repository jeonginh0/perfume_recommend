package jeonginho.perfume_recommend.Entity;

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
@Document(collection = "wishlist")
public class Wishlist {

    @Id
    private String id;
    private String userId;
    private List<String> perfumeIds;  // 여러 개의 향수를 담기 위한 List
}
