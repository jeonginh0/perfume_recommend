package jeonginho.perfume_recommend.repository.wishlist;

import jeonginho.perfume_recommend.Entity.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends MongoRepository<Wishlist, String> {
    // 특정 유저의 찜 목록을 가져온다
    List<Wishlist> findByUserId(String userId);

    // 특정 유저가 특정 향수를 찜했는지 확인한다
    Wishlist findByUserIdAndPerfumeId(String userId, String perfumeId);


}
