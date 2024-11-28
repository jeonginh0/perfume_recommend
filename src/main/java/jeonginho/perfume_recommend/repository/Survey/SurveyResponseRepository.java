package jeonginho.perfume_recommend.repository.Survey;

import jeonginho.perfume_recommend.Entity.survey.SurveyResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SurveyResponseRepository extends MongoRepository<SurveyResponse, String> {

    List<SurveyResponse> findByUserId(String userId);  // 회원의 응답 찾기
    List<SurveyResponse> findByGuestSessionId(String guestSessionId);  // 비회원의 응답 찾기
}
