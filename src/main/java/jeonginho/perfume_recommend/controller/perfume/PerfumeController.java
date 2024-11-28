package jeonginho.perfume_recommend.controller.perfume;

import jeonginho.perfume_recommend.Entity.perfume.Perfume;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import jeonginho.perfume_recommend.repository.Survey.SurveyResponseRepository;
import jeonginho.perfume_recommend.repository.perfume.PerfumeRepository;
import jeonginho.perfume_recommend.repository.recommend.RecommendGuestPerfumeRepository;
import jeonginho.perfume_recommend.service.recommend.RecommendGuestPerfumeService;
import jeonginho.perfume_recommend.service.recommend.RecommendPerfumesService;
import jeonginho.perfume_recommend.service.survey.SurveyService;
import jeonginho.perfume_recommend.service.perfume.PerfumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/perfumes")
public class PerfumeController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private RecommendPerfumesService recommendPerfumesService;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RecommendGuestPerfumeService recommendGuestPerfumeService;

    @Autowired
    private RecommendGuestPerfumeRepository recommendGuestPerfumeRepository;

    private final PerfumeRepository perfumeRepository;

    private final PerfumeService perfumeService;

    public PerfumeController(PerfumeRepository perfumeRepository, PerfumeService perfumeService) {
        this.perfumeRepository = perfumeRepository;
        this.perfumeService = perfumeService;
    }

    @Value("${json.file.path}")
    private String jsonFilePath;

    // 전체 향수 json 데이터 디비 저장 API
    @PostMapping("/importData")
    public void importData() throws IOException {
        perfumeService.importPerfumesJson(jsonFilePath);
    }

    // 향수 필터링 - 전체 향수 조회 API
    @GetMapping("/search/all")
    public ResponseEntity<List<Perfume>> getPerfumesAll() {
        List<Perfume> perfumes = perfumeService.getAllPerfumes();

        if (perfumes.isEmpty()) {
            return ResponseEntity.noContent().build();  // 향수가 없으면 204 No Content 반환
        } else {
            return ResponseEntity.ok(perfumes);  // 향수가 있으면 리스트 반환
        }
    }

    // 향수 필터링 - 단일/다중 향수 조회 API
    @GetMapping("/search/filter")
    public ResponseEntity<List<Perfume>> getPerfumesBrandDuration(
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> durations) {

        // 파라미터 출력 (디버깅용)
        System.out.println("브랜드 필터: " + brands);
        System.out.println("지속시간 필터: " + durations);

        List<Perfume> perfumes = perfumeService.getPerfumesBrandDuration(brands, durations);

        if (perfumes.isEmpty()) {
            System.out.println("해당 조건에 맞는 향수를 찾지 못했습니다.");
            return ResponseEntity.noContent().build();  // 조건에 맞는 향수가 없으면 204 No Content 반환
        } else {
            System.out.println("향수 목록: " + perfumes);
            return ResponseEntity.ok(perfumes);  // 조건에 맞는 향수가 있으면 리스트 반환
        }
    }

}