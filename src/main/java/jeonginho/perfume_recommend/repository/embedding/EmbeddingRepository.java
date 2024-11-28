package jeonginho.perfume_recommend.repository.embedding;

import jeonginho.perfume_recommend.Entity.embedding.Embedding;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EmbeddingRepository extends MongoRepository<Embedding, String> {
    Optional<Embedding> findByPerfumeId(String perfumeId);
}
