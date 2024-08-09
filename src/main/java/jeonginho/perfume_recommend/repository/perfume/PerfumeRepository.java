package jeonginho.perfume_recommend.repository.perfume;

import jeonginho.perfume_recommend.Entity.Perfume;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PerfumeRepository extends MongoRepository<Perfume, String> {
    List<Perfume> findByTopnoteContaining(String note);
    List<Perfume> findByMiddlenoteContaining(String note);
    List<Perfume> findByBasenoteContaining(String note);
    List<Perfume> findBySinglenoteContaining(String note);
    List<Perfume> findByDurationContaining(String duration);
}
