package jeonginho.perfume_recommend.repository.community;

import jeonginho.perfume_recommend.Entity.community.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByTitleContaining(String title);  // 제목으로 검색
    List<Post> findByUserId(String userId);  // 작성자 ID로 검색
}
