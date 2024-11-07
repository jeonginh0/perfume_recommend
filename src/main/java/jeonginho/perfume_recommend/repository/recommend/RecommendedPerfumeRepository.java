package jeonginho.perfume_recommend.repository.recommend;

import jeonginho.perfume_recommend.Entity.recommend.RecommendedPerfume;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecommendedPerfumeRepository extends MongoRepository<RecommendedPerfume, String> {
}
