package jeonginho.perfume_recommend.service.perfume;

import jeonginho.perfume_recommend.Entity.perfume.PerfumeComment;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import jeonginho.perfume_recommend.dto.perfume.PerfumeCommentRequest;
import jeonginho.perfume_recommend.repository.perfume.PerfumeCommentRepository;
import jeonginho.perfume_recommend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerfumeCommentService {
    @Autowired
    private UserService userService;
    private final PerfumeCommentRepository perfumeCommentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public PerfumeComment addComment(String token, PerfumeCommentRequest request) {
        String tokenWithoutBearer = token.replace("Bearer ", "");
        String userId = jwtTokenProvider.getUserIdFromJWT(tokenWithoutBearer);
        String nickname = userService.getNicknameById(userId);  // 이메일이 아닌 닉네임을 가져오는 메서드

        // PerfumeComment 객체를 새로 생성
        PerfumeComment newComment = new PerfumeComment();
        newComment.setUserId(userId);
        newComment.setNickname(nickname);  // 닉네임을 설정
        newComment.setPerfumeId(request.getPerfumeId());
        newComment.setComment(request.getComment());
        newComment.setCreatedAt(LocalDateTime.now());
        return perfumeCommentRepository.save(newComment);
    }

    public PerfumeComment getComment(String commentId) {
        return perfumeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
    }

    public List<PerfumeComment> getCommentsByPerfumeId(String perfumeId) {
        return perfumeCommentRepository.findByPerfumeId(perfumeId);
    }

    public void deleteComment(String token, String commentId) {
        String userId = jwtTokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
        PerfumeComment comment = perfumeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        perfumeCommentRepository.delete(comment);
    }

    public PerfumeComment updateComment(String token, String commentId, PerfumeCommentRequest request) {
        String userId = jwtTokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
        PerfumeComment comment = perfumeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        comment.setComment(request.getComment());
        comment.setUpdatedAt(LocalDateTime.now());
        return perfumeCommentRepository.save(comment);
    }
}
