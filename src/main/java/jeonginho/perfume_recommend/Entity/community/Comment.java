package jeonginho.perfume_recommend.Entity.community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("comments")
public class Comment {
    @Id
    private String id;

    private String postId;
    private String userId;
    private String content;
    private LocalDateTime createdAt;
}
