package jeonginho.perfume_recommend.dto.perfume;

import lombok.Data;

@Data
public class PerfumeCommentRequest {
    private String perfumeId; // 퍼퓸 테이블의 향수 ID
    private String comment;    // 댓글 내용
}
