package jeonginho.perfume_recommend.controller.community;

import jakarta.servlet.http.HttpServletRequest;
import jeonginho.perfume_recommend.Entity.community.Post;
import jeonginho.perfume_recommend.dto.community.post.PostRequest;
import jeonginho.perfume_recommend.service.community.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // 게시글 생성
    @PostMapping
    public Post createPost(@RequestBody PostRequest postRequest, @RequestHeader("Authorization") String token) {
        // Bearer 문자열 제거
        String tokenWithoutBearer = token.replace("Bearer ", "");
        return postService.createPost(postRequest.getTitle(), postRequest.getContent(), tokenWithoutBearer);
    }

    // 게시글 전체 조회
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    // 게시글 ID로 조회 (댓글 포함)
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostWithComments(@PathVariable String id) {
        try {
            Post post = postService.getPostWithComments(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 제목으로 게시글 조회
    @GetMapping("/title")
    public List<Post> getPostsByTitle(@RequestParam String title) {
        return postService.getPostsByTitle(title);
    }

    // 작성자 닉네임으로 게시글 조회
    @GetMapping("/nickname")
    public List<Post> getPostsByNickname(@RequestParam String nickname) {
        return postService.getPostsByNickname(nickname);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable String id, @RequestBody PostRequest postRequest, @RequestHeader("Authorization") String token) {
        // Bearer 문자열 제거
        String tokenWithoutBearer = token.replace("Bearer ", "");
        return postService.updatePost(id, postRequest.getTitle(), postRequest.getContent(), tokenWithoutBearer);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable String id, @RequestHeader("Authorization") String token) {
        // Bearer 문자열 제거
        String tokenWithoutBearer = token.replace("Bearer ", "");
        postService.deletePost(id, tokenWithoutBearer);
    }
}
