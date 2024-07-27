package jeonginho.perfume_recommend.service.note;

import jeonginho.perfume_recommend.model.note.SeasonNote;
import jeonginho.perfume_recommend.repository.note.SeasonNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeasonNoteService {

    @Autowired
    private SeasonNoteRepository seasonNoteRepository;

    public void saveNotes(SeasonNote note) {
        seasonNoteRepository.save(note);
    }
}
