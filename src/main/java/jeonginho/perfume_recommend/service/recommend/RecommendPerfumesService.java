package jeonginho.perfume_recommend.service.recommend;

import jakarta.servlet.http.HttpSession;
import jeonginho.perfume_recommend.Entity.perfume.Perfume;
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
        Optional<RecommendPerfumes> existingRecommendOpt = recommendPerfumesRepository.findByUserId(userId);

        if (existingRecommendOpt.isPresent()) {
            RecommendPerfumes existingRecommend = existingRecommendOpt.get();
            existingRecommend.setPerfumeIds(
                    recommendedPerfumes.stream().map(Perfume::getId).collect(Collectors.toList())
            );
            recommendPerfumesRepository.save(existingRecommend);
            System.out.println("기존 추천 데이터를 업데이트했습니다: " + existingRecommend);
        } else {
            RecommendPerfumes newRecommend = RecommendPerfumes.builder()
                    .userId(userId)
                    .perfumeIds(
                            recommendedPerfumes.stream().map(Perfume::getId).collect(Collectors.toList())
                    )
                    .build();
            recommendPerfumesRepository.save(newRecommend);
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
        SurveyResponse surveyResponse = (SurveyResponse) session.getAttribute("비회원 응답");

        if (surveyResponse == null) {
            System.out.println("세션에서 SurveyResponse를 찾지 못했습니다.");
            return List.of();
        }

        System.out.println("세션에서 SurveyResponse를 성공적으로 가져왔습니다: " + surveyResponse);

        List<Perfume> recommendedPerfumes = surveyService.processSurveyAndRecommendForGuest(surveyResponse);

        // 추천된 향수를 세션에 저장
        session.setAttribute("비회원 추천 결과", recommendedPerfumes);

        System.out.println("비회원 추천 결과를 세션에 저장했습니다: " + recommendedPerfumes);

        return recommendedPerfumes;
    }

    // 비회원의 세션 ID를 기반으로 추천된 향수 조회
    public List<Perfume> getGuestRecommendedPerfumes(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Perfume> recommendedPerfumes = (List<Perfume>) session.getAttribute("비회원 추천 결과");

        if (recommendedPerfumes == null || recommendedPerfumes.isEmpty()) {
            System.out.println("세션에서 추천된 향수를 찾지 못했습니다.");
            return List.of();
        }

        System.out.println("세션에서 추천된 향수를 성공적으로 가져왔습니다: " + recommendedPerfumes);
        return recommendedPerfumes;
    }
}