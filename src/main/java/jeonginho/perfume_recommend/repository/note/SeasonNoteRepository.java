package jeonginho.perfume_recommend.repository.note;

import jeonginho.perfume_recommend.Entity.note.SeasonNote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonNoteRepository extends MongoRepository<SeasonNote, String> {
    SeasonNote findBySeason(String season);
}
