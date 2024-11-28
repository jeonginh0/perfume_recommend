package jeonginho.perfume_recommend.service.community;

import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.Entity.community.Comment;
import jeonginho.perfume_recommend.Entity.community.Post;
import jeonginho.perfume_recommend.repository.community.CommentRepository;
import jeonginho.perfume_recommend.repository.community.PostRepository;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import jeonginho.perfume_recommend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User getUserFromToken(String token) {
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 게시글 생성
    public Post createPost(String title, String content, String token) {
        if (!jwtUtil.validateToken(token, jwtUtil.extractEmail(token))) {
            throw new RuntimeException("Invalid or expired token");
        }

        String userId = getUserIdFromToken(token);
        Post post = Post.builder()
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .userId(userId)  // User ID를 저장
                .build();
        return postRepository.save(post);
    }

    // 게시글 및 댓글 조회
    public Post getPostWithComments(String postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            List<Comment> comments = commentRepository.findByPostId(postId);
            post.setComments(comments); // 게시글 엔티티에 댓글을 추가 (게시글 엔티티에 댓글 리스트가 있어야 함)
            return post;
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    // 게시글 전체 조회
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 게시글 ID로 조회
    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    // 제목으로 게시글 조회
    public List<Post> getPostsByTitle(String title) {
        return postRepository.findByTitleContaining(title);
    }

    // 작성자 닉네임으로 게시글 조회
    public List<Post> getPostsByNickname(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        if (user.isPresent()) {
            return postRepository.findByUserId(user.get().getId());
        }
        return List.of();  // 사용자 없음
    }

    // 게시글 수정
    public Post updatePost(String id, String title, String content, String token) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = getUserFromToken(token);

        if (post.getUserId().equals(user.getId())) {
            post.setTitle(title);
            post.setContent(content);
            return postRepository.save(post);
        } else {
            throw new RuntimeException("Unauthorized");
        }
    }

    // 게시글 삭제
    public void deletePost(String id, String token) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = getUserFromToken(token);

        if (post.getUserId().equals(user.getId())) {
            postRepository.deleteById(id);
        } else {
            throw new RuntimeException("Unauthorized");
        }
    }

    // 현재 로그인한 사용자의 ID 가져오기
    private String getUserIdFromToken(String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
