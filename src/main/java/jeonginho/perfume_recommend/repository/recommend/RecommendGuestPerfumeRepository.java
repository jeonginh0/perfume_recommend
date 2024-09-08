package jeonginho.perfume_recommend.repository.recommend;

import jeonginho.perfume_recommend.Entity.recommend.RecommendGuestPerfumes;
import jeonginho.perfume_recommend.Entity.recommend.RecommendPerfumes;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RecommendGuestPerfumeRepository extends MongoRepository<RecommendGuestPerfumes, String> {
    Optional<RecommendGuestPerfumes> findByGuestId(String guestId); // guestId로 조회
}
