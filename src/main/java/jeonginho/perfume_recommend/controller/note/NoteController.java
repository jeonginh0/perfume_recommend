package jeonginho.perfume_recommend.controller.note;

import jeonginho.perfume_recommend.model.note.SeasonNote;
import jeonginho.perfume_recommend.model.note.CategoryNote;
import jeonginho.perfume_recommend.model.note.SituationNote;
import jeonginho.perfume_recommend.service.note.SeasonNoteService;
import jeonginho.perfume_recommend.service.note.CategoryNoteService;
import jeonginho.perfume_recommend.service.note.SituationNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private SeasonNoteService seasonNoteService;

    @Autowired
    private CategoryNoteService categoryNoteService;

    @Autowired
    private SituationNoteService situationNoteService;

    @PostMapping("/importSeasonNotes")
    public void importSeasonNotes(@RequestBody SeasonNote note) {
        seasonNoteService.saveNotes(note);
    }

    @PostMapping("/importTypeNotes")
    public void importTypeNotes(@RequestBody CategoryNote note) {
        categoryNoteService.saveNotes(note);
    }

    @PostMapping("/importSituationNotes")
    public void importSituationNotes(@RequestBody SituationNote note) {
        situationNoteService.saveNotes(note);
    }
}
