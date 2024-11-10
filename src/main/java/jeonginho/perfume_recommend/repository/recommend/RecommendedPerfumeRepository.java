package jeonginho.perfume_recommend.repository.recommend;

import jeonginho.perfume_recommend.Entity.recommend.RecommendedPerfume;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecommendedPerfumeRepository extends MongoRepository<RecommendedPerfume, String> {
    List<RecommendedPerfume> findByUserId(String userId);
}
