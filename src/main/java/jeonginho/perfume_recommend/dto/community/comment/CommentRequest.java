package jeonginho.perfume_recommend.dto.community.comment;

import lombok.Data;

@Data
public class CommentRequest {
    private String postId;
    private String content;
}
