package jeonginho.perfume_recommend.service.perfume;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeonginho.perfume_recommend.model.Perfume;
import jeonginho.perfume_recommend.model.User;
import jeonginho.perfume_recommend.model.note.CategoryNote;
import jeonginho.perfume_recommend.model.note.SeasonNote;
import jeonginho.perfume_recommend.model.note.SituationNote;
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

    // 사용자에게 향수를 추천하는 메서드
    public List<Perfume> recommendPerfumes(String userId) {
        // 사용자를 userId로 검색
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        // 사용자가 선호하는 노트, 계절, 상황, 지속 시간을 각각 Set으로 가져옴
        Set<String> preferredNotes = new HashSet<>(user.getPreferenceNote());
        Set<String> preferredSeasons = new HashSet<>(user.getPreferenceSeason());
        Set<String> preferredSituations = new HashSet<>(user.getPreferenceSituation());
        Set<String> preferredDurations = new HashSet<>(user.getPreferenceDuration());

        // 매칭되는 노트, 계절, 상황을 저장할 Set 생성
        Set<String> matchingNotes = new HashSet<>();
        Set<String> matchingSeasons = new HashSet<>();
        Set<String> matchingSituations = new HashSet<>();

        // 사용자가 선호하는 노트와 매칭되는 노트들을 가져와서 matchingNotes에 추가
        for (String note : preferredNotes) {
            CategoryNote categoryNote = categoryNoteRepository.findByCategory(note);
            if (categoryNote != null) {
                matchingNotes.addAll(categoryNote.getNotes());
            }
        }

        // 사용자가 선호하는 계절과 매칭되는 노트들을 가져와서 matchingSeasons에 추가
        for (String season : preferredSeasons) {
            SeasonNote seasonNote = seasonNoteRepository.findBySeason(season);
            if (seasonNote != null) {
                matchingSeasons.addAll(seasonNote.getNotes());
            }
        }

        // 사용자가 선호하는 상황과 매칭되는 노트들을 가져와서 matchingSituations에 추가
        for (String situation : preferredSituations) {
            SituationNote situationNote = situationNoteRepository.findBySituation(situation);
            if (situationNote != null) {
                matchingSituations.addAll(situationNote.getNotes());
            }
        }

        // 모든 향수를 가져와서 사용자의 선호 조건과 일치하는 향수 필터링
        List<Perfume> allPerfumes = perfumeRepository.findAll();
        List<Perfume> matchingPerfumes = allPerfumes.stream()
                .filter(perfume -> matchesPreferences(perfume, matchingNotes, matchingSeasons, matchingSituations, preferredDurations))
                .collect(Collectors.toList());

        // 필터링된 향수 리스트를 섞음
        Collections.shuffle(matchingPerfumes);

        // 최대 10개의 향수를 추천 리스트로 반환
        return matchingPerfumes.size() > 10 ? matchingPerfumes.subList(0, 10) : matchingPerfumes;
    }

    // 향수가 사용자의 선호 조건과 일치하는지 확인하는 메서드
    private boolean matchesPreferences(Perfume perfume, Set<String> matchingNotes,
                                       Set<String> matchingSeasons, Set<String> matchingSituations,
                                       Set<String> preferredDurations) {
        boolean notesMatch = matchingNotes.stream().anyMatch(note -> containsNote(perfume, note));
        boolean seasonMatch = matchingSeasons.stream().anyMatch(season -> containsNote(perfume, season));
        boolean situationMatch = matchingSituations.stream().anyMatch(situation -> containsNote(perfume, situation));
        boolean durationMatch = preferredDurations.contains(perfume.getDuration());

        // 노트, 계절, 상황, 지속 시간 중 하나라도 일치하면 true 반환
        return notesMatch && seasonMatch && situationMatch && durationMatch;
    }

    // 향수가 특정 노트를 포함하고 있는지 확인하는 메서드
    private boolean containsNote(Perfume perfume, String note) {
        return (perfume.getSinglenote() != null && perfume.getSinglenote().contains(note)) ||
                (perfume.getTopnote() != null && perfume.getTopnote().contains(note)) ||
                (perfume.getMiddlenote() != null && perfume.getMiddlenote().contains(note)) ||
                (perfume.getBasenote() != null && perfume.getBasenote().contains(note));
    }
}
