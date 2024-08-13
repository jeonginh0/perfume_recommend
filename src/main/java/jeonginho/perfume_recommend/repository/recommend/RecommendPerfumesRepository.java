package jeonginho.perfume_recommend.repository.recommend;

import jeonginho.perfume_recommend.Entity.recommend.RecommendPerfumes;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RecommendPerfumesRepository extends MongoRepository<RecommendPerfumes, String> {
    Optional<RecommendPerfumes> findByUserId(String userId);
}
