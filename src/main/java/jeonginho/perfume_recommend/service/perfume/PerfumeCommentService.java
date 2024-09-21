package jeonginho.perfume_recommend.service.perfume;

import jeonginho.perfume_recommend.Entity.perfume.PerfumeComment;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import jeonginho.perfume_recommend.repository.perfume.PerfumeCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerfumeCommentService {
    private final PerfumeCommentRepository perfumeCommentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public PerfumeComment addComment(String token, PerfumeComment comment) {
        String userId = jwtTokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
        comment.setUserId(userId); // JWT에서 추출한 유저 ID 설정
        comment.setCreatedAt(LocalDateTime.now());
        return perfumeCommentRepository.save(comment);
    }

    public PerfumeComment getComment(String commentId) {
        return perfumeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public List<PerfumeComment> getCommentsByPerfumeId(String perfumeId) {
        return perfumeCommentRepository.findByPerfumeId(perfumeId);
    }

    public void deleteComment(String token, String commentId) {
        String userId = jwtTokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
        PerfumeComment comment = perfumeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        perfumeCommentRepository.delete(comment);
    }

    public PerfumeComment updateComment(String token, String commentId, PerfumeComment updatedComment) {
        String userId = jwtTokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
        PerfumeComment comment = perfumeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        comment.setComment(updatedComment.getComment());
        comment.setUpdatedAt(LocalDateTime.now());
        return perfumeCommentRepository.save(comment);
    }
}
