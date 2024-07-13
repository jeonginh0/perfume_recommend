package jeonginho.perfume_recommend.repository.Perfume;

import jeonginho.perfume_recommend.model.Perfume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PerfumeRepository extends MongoRepository<Perfume, String> {

    @Query("{'duration': ?0, 'acode': { $in: ?1 } }")
    List<Perfume> findByDurationAndAcodeIn(String duration, List<String> acode);
}
