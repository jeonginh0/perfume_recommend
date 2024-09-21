package jeonginho.perfume_recommend.controller.perfume;

import jeonginho.perfume_recommend.Entity.perfume.PerfumeComment;
import jeonginho.perfume_recommend.service.perfume.PerfumeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfume-comments")
@RequiredArgsConstructor
public class PerfumeCommentController {
    private final PerfumeCommentService perfumeCommentService;

    // 작성
    @PostMapping
    public PerfumeComment addComment(@RequestHeader("Authorization") String token, @RequestBody PerfumeComment comment) {
        return perfumeCommentService.addComment(token, comment);
    }

    // 테스트용 댓글 조회
    @GetMapping("/{commentId}")
    public PerfumeComment getComment(@PathVariable String commentId) {
        System.out.println("댓글 작성 완료");
        return perfumeCommentService.getComment(commentId);
    }

    // 향수에 대한 댓글 조회
    @GetMapping("/detail/{perfumeId}")
    public ResponseEntity<List<PerfumeComment>> getCommentsByPerfumeId(@PathVariable String perfumeId) {
        List<PerfumeComment> comments = perfumeCommentService.getCommentsByPerfumeId(perfumeId);
        return ResponseEntity.ok(comments);
    }

    // 삭제
    @DeleteMapping("/{commentId}")
    public void deleteComment(@RequestHeader("Authorization") String token, @PathVariable String commentId) {
        System.out.println("댓글 삭제 완료");
        perfumeCommentService.deleteComment(token, commentId);
    }

    // 수정
    @PutMapping("/{commentId}")
    public PerfumeComment updateComment(@RequestHeader("Authorization") String token, @PathVariable String commentId, @RequestBody PerfumeComment comment) {
        System.out.println("댓글 수정 완료");
        return perfumeCommentService.updateComment(token, commentId, comment);
    }
}
