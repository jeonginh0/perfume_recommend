package jeonginho.perfume_recommend.repository.note;

import jeonginho.perfume_recommend.model.note.SituationNote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SituationNoteRepository extends MongoRepository<SituationNote, String> {
    SituationNote findBySituation(String situation);
}
