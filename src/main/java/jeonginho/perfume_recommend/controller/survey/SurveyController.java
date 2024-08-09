package jeonginho.perfume_recommend.controller.survey;

import jakarta.servlet.http.HttpSession;
import jeonginho.perfume_recommend.Entity.survey.SurveyResponse;
import jeonginho.perfume_recommend.repository.Survey.SurveyResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/survey")
public class SurveyController {

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    // 회원 응답 저장
    @PostMapping("/response/member/{userId}")
    public ResponseEntity<String> saveMemberSurveyResponse(@RequestBody SurveyResponse surveyResponse) {
        if (surveyResponse.getUserId() == null) {
            return ResponseEntity.badRequest().body("회원 ID 필요.");
        }
        surveyResponseRepository.save(surveyResponse);
        return ResponseEntity.ok("회원 설문 응답 저장완료.");
    }

    // 비회원(게스트) 응답 저장
    @PostMapping("/response/guest")
    public ResponseEntity<String> saveGuestSurveyResponse(@RequestBody SurveyResponse surveyResponse, HttpSession session) {
        // 세션 ID를 설정
        if (surveyResponse.getGuestSessionId() == null) {
            surveyResponse.setGuestSessionId(session.getId());
        }
        surveyResponseRepository.save(surveyResponse);
        return ResponseEntity.ok("비회원 설문 응답 저장완료.");
    }
}
