package jeonginho.perfume_recommend.service.survey;

import jeonginho.perfume_recommend.Entity.survey.SurveyResponse;
import jeonginho.perfume_recommend.Entity.Perfume;
import jeonginho.perfume_recommend.service.perfume.PerfumeService;
import jeonginho.perfume_recommend.repository.Survey.SurveyResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private PerfumeService perfumeService;

    // 회원일 때 추천
    public List<Perfume> processSurveyAndRecommendForMember(String userId) {
        List<SurveyResponse> responses = surveyResponseRepository.findByUserId(userId);

        System.out.println("향수 추천받기를 시도한 회원 ID:" + userId);
        if (responses.isEmpty()) {
            System.out.println("No survey responses found for user ID: " + userId);
            return List.of(); // 빈 리스트 반환
        }

        // 가장 높은 가중치의 응답을 우선적으로 사용하여 필터링
        SurveyResponse.ResponseItem highestPriorityItem = getHighestPriorityResponse(responses);

        if (highestPriorityItem != null) {
            List<Perfume> recommendedPerfumes = filterPerfumesByResponse(highestPriorityItem);
            return limitPerfumes(recommendedPerfumes, 10);
        }

        return List.of(); // 매칭된 향수가 없을 경우 빈 리스트 반환
    }

    // 비회원일 때 추천
    public List<Perfume> processSurveyAndRecommendForGuest(String sessionId) {
        List<SurveyResponse> responses = surveyResponseRepository.findByGuestSessionId(sessionId);

        System.out.println("향수 추천받기를 시도한 세션 ID:" + sessionId);
        if (responses.isEmpty()) {
            System.out.println("No survey responses found for session ID: " + sessionId);
            return List.of(); // 빈 리스트 반환
        }

        // 가장 높은 가중치의 응답을 우선적으로 사용하여 필터링
        SurveyResponse.ResponseItem highestPriorityItem = getHighestPriorityResponse(responses);

        if (highestPriorityItem != null) {
            List<Perfume> recommendedPerfumes = filterPerfumesByResponse(highestPriorityItem);
            return limitPerfumes(recommendedPerfumes, 10);
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

    private List<Perfume> limitPerfumes(List<Perfume> perfumes, int limit) {
        // 향수 리스트의 크기를 제한
        return perfumes.stream().limit(limit).collect(Collectors.toList());
    }
}
