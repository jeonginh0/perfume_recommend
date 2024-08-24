package jeonginho.perfume_recommend.controller.community;

import jeonginho.perfume_recommend.Entity.community.Comment;
import jeonginho.perfume_recommend.dto.community.comment.CommentRequest;
import jeonginho.perfume_recommend.service.community.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postId}")
    public Comment createComment(@PathVariable String postId,
                                 @RequestBody CommentRequest commentRequest,
                                 @RequestHeader("Authorization") String token) {
        String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
        return commentService.createComment(postId, commentRequest.getContent(), tokenWithoutBearer);
    }

    // 댓글 수정
    @PutMapping("/update/{id}")
    public Comment updateComment(@PathVariable String id,
                                 @RequestBody CommentRequest commentRequest,
                                 @RequestHeader("Authorization") String token) {
        String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
        return commentService.updateComment(id, commentRequest.getContent(), tokenWithoutBearer);
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{id}")
    public void deleteComment(@PathVariable String id,
                              @RequestHeader("Authorization") String token) {
        String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
        commentService.deleteComment(id, tokenWithoutBearer);
    }
}
