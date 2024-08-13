package jeonginho.perfume_recommend.service.recommend;

import jakarta.servlet.http.HttpSession;
import jeonginho.perfume_recommend.Entity.Perfume;
import jeonginho.perfume_recommend.Entity.recommend.RecommendPerfumes;
import jeonginho.perfume_recommend.Entity.survey.SurveyResponse;
import jeonginho.perfume_recommend.repository.perfume.PerfumeRepository;
import jeonginho.perfume_recommend.repository.recommend.RecommendPerfumesRepository;
import jeonginho.perfume_recommend.service.survey.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendPerfumesService {

    @Autowired
    private RecommendPerfumesRepository recommendPerfumesRepository;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private PerfumeRepository perfumeRepository;

    // 설문 응답으로 추천받은 향수 DB 저장 (회원에 한하여)
    public void saveRecommendPerfumes(String userId, List<Perfume> recommendedPerfumes) {
        // 기존에 해당 회원의 추천 데이터가 있는지 확인
        Optional<RecommendPerfumes> existingRecommendOpt = recommendPerfumesRepository.findByUserId(userId);

        if (existingRecommendOpt.isPresent()) {
            // 기존 데이터를 업데이트
            RecommendPerfumes existingRecommend = existingRecommendOpt.get();
            existingRecommend.setPerfumeIds(
                    recommendedPerfumes.stream().map(Perfume::getId).collect(Collectors.toList())
            );
            recommendPerfumesRepository.save(existingRecommend); // 추천받은 향수 DB에 저장 (업데이트)
            System.out.println("기존 추천 데이터를 업데이트했습니다: " + existingRecommend);
        } else {
            // 새로운 추천 데이터를 저장
            RecommendPerfumes newRecommend = RecommendPerfumes.builder()
                    .userId(userId)
                    .perfumeIds(
                            recommendedPerfumes.stream().map(Perfume::getId).collect(Collectors.toList())
                    )
                    .build();
            recommendPerfumesRepository.save(newRecommend); // 추천받은 향수 DB에 저장 (신규 저장)
            System.out.println("새로운 추천 데이터를 저장했습니다: " + newRecommend);
        }
    }

    // 사용자별 추천된 향수들의 상세 정보를 조회
    public List<Perfume> getRecommendPerfumesUser(String userId) {
        Optional<RecommendPerfumes> recommendations = recommendPerfumesRepository.findByUserId(userId);

        List<String> perfumeIds = recommendations.stream()
                .flatMap(recommendation -> recommendation.getPerfumeIds().stream())
                .collect(Collectors.toList());

        return perfumeRepository.findAllById(perfumeIds);
    }

    // 비회원 설문 응답 처리 및 추천된 향수 세션 저장
    public List<Perfume> recommendForGuest(HttpSession session) {
        // 세션에서 "비회원 응답" 가져오기
        SurveyResponse surveyResponse = (SurveyResponse) session.getAttribute("비회원 응답");

        if (surveyResponse == null) {
            System.out.println("세션에서 SurveyResponse를 찾지 못했습니다.");
            return List.of();  // 세션에 응답이 없으면 빈 리스트 반환
        }

        System.out.println("세션에서 SurveyResponse를 성공적으로 가져왔습니다: " + surveyResponse);

        // 설문 응답을 바탕으로 추천 수행
        List<Perfume> recommendedPerfumes = surveyService.processSurveyAndRecommendForGuest(surveyResponse);

        // 추천된 향수를 세션에 저장
        session.setAttribute("비회원 추천 결과", recommendedPerfumes);

        System.out.println("비회원 추천 결과를 세션에 저장했습니다: " + recommendedPerfumes);

        return recommendedPerfumes;
    }

    // 비회원의 세션 ID를 기반으로 추천된 향수 조회
    public List<Perfume> getGuestRecommendedPerfumes(HttpSession session) {
        // 세션에서 "비회원 추천 결과" 가져오기
        @SuppressWarnings("unchecked")
        List<Perfume> recommendedPerfumes = (List<Perfume>) session.getAttribute("비회원 추천 결과");

        if (recommendedPerfumes == null || recommendedPerfumes.isEmpty()) {
            System.out.println("세션에서 추천된 향수를 찾지 못했습니다.");
            return List.of();  // 비어 있는 경우 빈 리스트 반환
        }

        System.out.println("세션에서 추천된 향수를 성공적으로 가져왔습니다: " + recommendedPerfumes);
        return recommendedPerfumes;
    }
}
