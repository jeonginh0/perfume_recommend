package jeonginho.perfume_recommend.service.note;

import jeonginho.perfume_recommend.Entity.note.SituationNote;
import jeonginho.perfume_recommend.repository.note.SituationNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SituationNoteService {

    @Autowired
    private SituationNoteRepository situationNoteRepository;

    public void saveNotes(SituationNote note) {
        situationNoteRepository.save(note);
    }
}
