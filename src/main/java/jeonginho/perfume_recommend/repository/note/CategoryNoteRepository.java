package jeonginho.perfume_recommend.repository.note;

import jeonginho.perfume_recommend.model.note.CategoryNote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryNoteRepository extends MongoRepository<CategoryNote, String> {
    CategoryNote findByCategory(String category);
}
