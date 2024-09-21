package jeonginho.perfume_recommend.repository.perfume;

import jeonginho.perfume_recommend.Entity.perfume.PerfumeComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PerfumeCommentRepository extends MongoRepository<PerfumeComment, String> {
    List<PerfumeComment> findByPerfumeId(String perfumeId);
}
