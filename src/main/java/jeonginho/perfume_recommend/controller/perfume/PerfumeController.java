package jeonginho.perfume_recommend.controller.perfume;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jeonginho.perfume_recommend.Entity.perfume.Perfume;
import jeonginho.perfume_recommend.Entity.recommend.RecommendGuestPerfumes;
import jeonginho.perfume_recommend.Entity.survey.SurveyResponse;
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
import java.util.Optional;

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

    // 회원일 때 향수 추천 및 저장 API
    @GetMapping("/recommend/member")
    public ResponseEntity<List<Perfume>> recommendSaveMember(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // "Bearer " 제거
        } else {
            return ResponseEntity.badRequest().body(List.of());  // 토큰이 없으면 빈 리스트 반환
        }

        String userId;
        try {
            userId = jwtTokenProvider.getUserIdFromJWT(token);
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of());  // 유효하지 않은 토큰인 경우 빈 리스트 반환
        }

        List<Perfume> recommendedPerfumes = surveyService.processSurveyAndRecommendForMember(userId);
        recommendPerfumesService.saveRecommendPerfumes(userId, recommendedPerfumes);

        System.out.println("회원 추천 향수 저장 완료!\n" + "회원: " + userId);
        return ResponseEntity.ok(recommendedPerfumes);
    }

    // 비회원(게스트) 추천 및 DB 저장 API
    @GetMapping("/recommend/guest")
    public ResponseEntity<List<Perfume>> recommendSaveGuest(
            @RequestParam(value = "surveyResponseId", required = false) String surveyResponseId) {

        if (surveyResponseId == null || surveyResponseId.isEmpty()) {
            return ResponseEntity.badRequest().body(List.of());  // 파라미터가 없으면 빈 리스트 반환
        }

        System.out.println("SurveyResponse ID: " + surveyResponseId);

        // 설문 응답 조회
        SurveyResponse surveyResponse = surveyResponseRepository.findById(surveyResponseId)
                .orElse(null);

        if (surveyResponse == null) {
            System.out.println("데이터베이스에서 설문 응답을 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body(List.of());  // 적절한 오류 메시지 반환
        } else {
            System.out.println("데이터베이스에서 설문 응답을 성공적으로 가져왔습니다: " + surveyResponse);
        }

        // 설문 응답을 바탕으로 향수 추천
        List<Perfume> recommendedPerfumes = surveyService.processSurveyAndRecommendForGuest(surveyResponse);

        if (recommendedPerfumes.isEmpty()) {
            return ResponseEntity.noContent().build();  // 추천된 향수가 없으면 204 No Content 반환
        }

        // 게스트 ID를 생성하거나 특정 값으로 설정
        String guestId = surveyResponseId;

        // 추천된 향수를 DB에 저장
        recommendGuestPerfumeService.saveRecommendPerfumes(guestId, recommendedPerfumes);

        System.out.println("게스트 추천 향수 저장 완료");
        return ResponseEntity.ok(recommendedPerfumes);  // 추천된 향수 리스트 반환
    }

    // 회원의 추천된 향수 조회 API
    @GetMapping("/recommend/member/details")
    public ResponseEntity<List<Perfume>> getDetailRecommendUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // "Bearer " 제거
        } else {
            return ResponseEntity.badRequest().body(List.of());  // 토큰이 없으면 빈 리스트 반환
        }

        String userId;
        try {
            userId = jwtTokenProvider.getUserIdFromJWT(token);
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of());  // 유효하지 않은 토큰인 경우 빈 리스트 반환
        }

        List<Perfume> perfumes = recommendPerfumesService.getRecommendPerfumesUser(userId);
        return ResponseEntity.ok(perfumes);
    }

    // 비회원 추천 결과 조회 API
    @GetMapping("/recommend/guest/details")
    public ResponseEntity<List<Perfume>> getDetailRecommendGuest(@RequestParam("guestId") String guestId) {
        // 데이터베이스에서 추천된 향수 조회
        Optional<RecommendGuestPerfumes> recommendGuestPerfumesOpt = recommendGuestPerfumeRepository.findByGuestId(guestId);

        if (recommendGuestPerfumesOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(List.of());  // 추천된 향수가 없으면 400 반환
        }

        RecommendGuestPerfumes recommendGuestPerfumes = recommendGuestPerfumesOpt.get();
        List<String> perfumeIds = recommendGuestPerfumes.getPerfumeIds();
        List<Perfume> recommendedPerfumes = perfumeRepository.findAllById(perfumeIds);

        return ResponseEntity.ok(recommendedPerfumes);  // 추천된 향수 리스트 반환
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