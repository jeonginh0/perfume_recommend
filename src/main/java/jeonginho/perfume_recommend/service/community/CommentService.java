package jeonginho.perfume_recommend.service.community;

import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.Entity.community.Comment;
import jeonginho.perfume_recommend.repository.community.CommentRepository;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import jeonginho.perfume_recommend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 댓글 작성
    public Comment createComment(String postId, String content, String token) {
        // 유효한 토큰인지 확인
        String email = jwtUtil.extractEmail(token);
        if (!jwtUtil.validateToken(token, email)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String userId = getUserIdFromEmail(email);

        Comment comment = Comment.builder()
                .postId(postId)
                .content(content)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }

    // 댓글 수정
    public Comment updateComment(String id, String content, String token) {
        // 유효한 토큰인지 확인
        String email = jwtUtil.extractEmail(token);
        if (!jwtUtil.validateToken(token, email)) {
            throw new RuntimeException("Invalid or expired token");
        }

        Optional<Comment> existingComment = commentRepository.findById(id);
        if (existingComment.isPresent()) {
            Comment comment = existingComment.get();
            String userId = getUserIdFromEmail(email);
            if (comment.getUserId().equals(userId)) {
                comment.setContent(content);
                return commentRepository.save(comment);
            } else {
                throw new RuntimeException("Not authorized to update this comment");
            }
        } else {
            throw new RuntimeException("Comment not found");
        }
    }

    // 댓글 삭제
    public void deleteComment(String id, String token) {
        // 유효한 토큰인지 확인
        String email = jwtUtil.extractEmail(token);
        if (!jwtUtil.validateToken(token, email)) {
            throw new RuntimeException("Invalid or expired token");
        }

        Optional<Comment> existingComment = commentRepository.findById(id);
        if (existingComment.isPresent()) {
            Comment comment = existingComment.get();
            String userId = getUserIdFromEmail(email);
            if (comment.getUserId().equals(userId)) {
                commentRepository.deleteById(id);
            } else {
                throw new RuntimeException("Not authorized to delete this comment");
            }
        } else {
            throw new RuntimeException("Comment not found");
        }
    }

    // 이메일로 사용자 ID 추출
    private String getUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
