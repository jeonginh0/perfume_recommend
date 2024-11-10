package jeonginho.perfume_recommend.repository.recommend;

import jeonginho.perfume_recommend.Entity.recommend.RecommendedPerfume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedPerfumeRepository extends MongoRepository<RecommendedPerfume, String> {
    List<RecommendedPerfume> findByUserId(String userId);
}