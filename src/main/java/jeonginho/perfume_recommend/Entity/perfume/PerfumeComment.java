package jeonginho.perfume_recommend.Entity.perfume;

import jeonginho.perfume_recommend.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("perfume_comments")
public class PerfumeComment {
    @Id
    private String id; // 댓글 ID

    private String perfumeId; // 퍼퓸테이블에 있는 향수 아이디

    private String userId; // 작성자 Id
    private String nickname; // 작성자 닉네임

    private String comment; // 댓글 내용

    private LocalDateTime createdAt; // 작성 시간

    private LocalDateTime updatedAt; // 수정 시간
}
