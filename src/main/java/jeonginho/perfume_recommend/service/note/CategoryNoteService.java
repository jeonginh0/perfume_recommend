package jeonginho.perfume_recommend.service.note;

import jeonginho.perfume_recommend.model.note.CategoryNote;
import jeonginho.perfume_recommend.repository.note.CategoryNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryNoteService {
    @Autowired
    private CategoryNoteRepository categoryNoteRepository;

    public void saveNotes(CategoryNote note) {
        categoryNoteRepository.save(note);
    }
}
