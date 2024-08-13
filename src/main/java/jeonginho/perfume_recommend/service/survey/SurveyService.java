package jeonginho.perfume_recommend.service.survey;

import jeonginho.perfume_recommend.Entity.survey.SurveyResponse;
import jeonginho.perfume_recommend.Entity.Perfume;
import jeonginho.perfume_recommend.repository.Survey.SurveyResponseRepository;
import jeonginho.perfume_recommend.service.perfume.PerfumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    @Autowired
    private PerfumeService perfumeService;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    // 회원일 때 추천
    public List<Perfume> processSurveyAndRecommendForMember(String userId) {
        // userId에 해당하는 SurveyResponse를 조회
        List<SurveyResponse> responses = surveyResponseRepository.findByUserId(userId);
        if (responses == null || responses.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }

        SurveyResponse.ResponseItem highestPriorityItem = getHighestPriorityResponse(responses);

        if (highestPriorityItem != null) {
            List<Perfume> recommendedPerfumes = filterPerfumesByResponse(highestPriorityItem);
            return getRandomPerfumes(recommendedPerfumes, 10);
        }

        return List.of(); // 매칭된 향수가 없을 경우 빈 리스트 반환
    }

    // 비회원일 때 추천 (세션 기반)
    public List<Perfume> processSurveyAndRecommendForGuest(SurveyResponse surveyResponse) {
        if (surveyResponse == null || surveyResponse.getResponses().isEmpty()) {
            System.out.println("No survey responses found for guest session");
            return List.of(); // 빈 리스트 반환
        }

        // 가장 높은 가중치의 응답을 우선적으로 사용하여 필터링
        SurveyResponse.ResponseItem highestPriorityItem = getHighestPriorityResponse(List.of(surveyResponse));

        if (highestPriorityItem != null) {
            List<Perfume> recommendedPerfumes = filterPerfumesByResponse(highestPriorityItem);
            return getRandomPerfumes(recommendedPerfumes, 10);
        }

        return List.of(); // 매칭된 향수가 없을 경우 빈 리스트 반환
    }

    private SurveyResponse.ResponseItem getHighestPriorityResponse(List<SurveyResponse> responses) {
        // 모든 설문 응답 항목 중 가장 가중치가 높은 항목을 선택
        return responses.stream()
                .flatMap(response -> response.getResponses().stream())
                .max(Comparator.comparingDouble(SurveyResponse.ResponseItem::getWeight))
                .orElse(null);
    }

    private List<Perfume> filterPerfumesByResponse(SurveyResponse.ResponseItem item) {
        // 응답 유형에 따라 필터링
        switch (item.getQuestionType()) {
            case "category":
                List<String> categoryNotes = perfumeService.getCategoryNotes(item.getResponse());
                return perfumeService.findMatchingPerfumesByNotes(categoryNotes);
            case "season":
                List<String> seasonNotes = perfumeService.getSeasonNotes(item.getResponse());
                return perfumeService.findMatchingPerfumesByNotes(seasonNotes);
            case "situation":
                List<String> situationNotes = perfumeService.getSituationNotes(item.getResponse());
                return perfumeService.findMatchingPerfumesByNotes(situationNotes);
            case "duration":
                return perfumeService.findPerfumesByDuration(item.getResponse());
            default:
                return List.of();
        }
    }

    private List<Perfume> getRandomPerfumes(List<Perfume> perfumes, int limit) {
        // 리스트를 섞어서 랜덤하게 만듭니다.
        Collections.shuffle(perfumes);
        // 최대 limit개의 향수를 반환합니다.
        return perfumes.stream().limit(limit).collect(Collectors.toList());
    }
}
