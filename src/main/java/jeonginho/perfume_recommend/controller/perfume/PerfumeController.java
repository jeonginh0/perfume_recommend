package jeonginho.perfume_recommend.controller.perfume;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jeonginho.perfume_recommend.Entity.Perfume;
import jeonginho.perfume_recommend.Entity.recommend.RecommendPerfumes;
import jeonginho.perfume_recommend.Entity.survey.SurveyResponse;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import jeonginho.perfume_recommend.repository.perfume.PerfumeRepository;
import jeonginho.perfume_recommend.repository.recommend.RecommendPerfumesRepository;
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
    private JwtTokenProvider jwtTokenProvider;

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

    // 비회원일 때 향수 추천 및 세션 저장 API
    @GetMapping("/recommend/guest")
    public ResponseEntity<List<Perfume>> recommendSaveGuest(HttpSession session) {

        SurveyResponse surveyResponse = (SurveyResponse) session.getAttribute("guestSurveyResponse");
        if (surveyResponse == null) {
            System.out.println("세션에 guestSurveyResponse가 없습니다.");
            return ResponseEntity.badRequest().body(List.of());  // 필요한 경우 적절한 오류 메시지 반환
        } else {
            System.out.println("세션에서 설문 응답을 성공적으로 가져왔습니다: " + surveyResponse);
        }

        // 설문 응답을 바탕으로 향수 추천
        List<Perfume> recommendedPerfumes = surveyService.processSurveyAndRecommendForGuest(surveyResponse);

        if (recommendedPerfumes.isEmpty()) {
            return ResponseEntity.noContent().build();  // 추천된 향수가 없으면 204 No Content 반환
        }

        // 추천된 향수를 세션에 저장
        session.setAttribute("guestRecommendedPerfumes", recommendedPerfumes);

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

    // 비회원의 추천된 향수 조회 API
    @GetMapping("/recommend/guest/details")
    public ResponseEntity<List<Perfume>> getDetailRecommendGuest(HttpSession session) {
        // 세션에서 추천된 향수를 조회
        List<Perfume> recommendedPerfumes = (List<Perfume>) session.getAttribute("guestRecommendedPerfumes");

        if (recommendedPerfumes == null || recommendedPerfumes.isEmpty()) {
            return ResponseEntity.badRequest().body(List.of());  // 추천된 향수가 없으면 400 반환
        }

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
