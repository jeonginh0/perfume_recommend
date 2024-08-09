package jeonginho.perfume_recommend.service.perfume;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeonginho.perfume_recommend.Entity.Perfume;
import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.Entity.note.CategoryNote;
import jeonginho.perfume_recommend.Entity.note.SeasonNote;
import jeonginho.perfume_recommend.Entity.note.SituationNote;
import jeonginho.perfume_recommend.repository.note.CategoryNoteRepository;
import jeonginho.perfume_recommend.repository.note.SeasonNoteRepository;
import jeonginho.perfume_recommend.repository.note.SituationNoteRepository;
import jeonginho.perfume_recommend.repository.perfume.PerfumeRepository;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerfumeService {

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryNoteRepository categoryNoteRepository;

    @Autowired
    private SeasonNoteRepository seasonNoteRepository;

    @Autowired
    private SituationNoteRepository situationNoteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    //json 파일 저장하는 메서드 (프로젝트 최초 1회 실행만 하면 됨)
    public void importPerfumesJson(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath); //리소스를 로드하기 위한 클래스 객체 선언
        InputStream inputStream = resource.getInputStream(); //파일로부터 데이터를 읽어오기 위한 객체 선언

        List<Perfume> perfumes = objectMapper.readValue(inputStream,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Perfume.class));

        perfumeRepository.saveAll(perfumes); // Perfume list 객체들을 DB에 저장
    }

    // 카테고리에 따른 노트 필터링
    public List<String> getCategoryNotes(String category) {
        CategoryNote categoryNote = categoryNoteRepository.findByCategory(category);
        System.out.println("Category Notes for " + category + ": " + (categoryNote != null ? categoryNote.getNotes() : "None"));
        return categoryNote != null ? categoryNote.getNotes() : List.of();

    }

    // 계절에 따른 노트 필터링
    public List<String> getSeasonNotes(String season) {
        SeasonNote seasonNote = seasonNoteRepository.findBySeason(season);
        return seasonNote != null ? seasonNote.getNotes() : List.of();
    }

    // 상황에 따른 노트 필터링
    public List<String> getSituationNotes(String situation) {
        SituationNote situationNote = situationNoteRepository.findBySituation(situation);
        return situationNote != null ? situationNote.getNotes() : List.of();
    }

    // 주어진 노트 리스트에 맞는 향수를 찾기
    public List<Perfume> findMatchingPerfumesByNotes(List<String> notes) {
        Set<Perfume> matchingPerfumes = new HashSet<>();

        for (String note : notes) {
            matchingPerfumes.addAll(perfumeRepository.findBySinglenoteContaining(note));
            matchingPerfumes.addAll(perfumeRepository.findByTopnoteContaining(note));
            matchingPerfumes.addAll(perfumeRepository.findByMiddlenoteContaining(note));
            matchingPerfumes.addAll(perfumeRepository.findByBasenoteContaining(note));
        }

        return matchingPerfumes.stream().collect(Collectors.toList());
    }

    // 지속 시간에 따라 향수를 찾기
    public List<Perfume> findPerfumesByDuration(String duration) {
        return perfumeRepository.findByDurationContaining(duration);
    }
}
