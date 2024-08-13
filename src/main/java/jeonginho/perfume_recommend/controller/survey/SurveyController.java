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

    // 회원 응답 저장 API
    @PostMapping("/response/member/{userId}")
    public ResponseEntity<String> saveMemberSurveyResponse(@RequestBody SurveyResponse surveyResponse) {
        if (surveyResponse.getUserId() == null) {
            return ResponseEntity.badRequest().body("회원 ID 필요.");
        }
        surveyResponseRepository.save(surveyResponse);
        return ResponseEntity.ok("회원 설문 응답 저장완료.");
    }

    @PostMapping("/response/guest")
    public ResponseEntity<String> saveGuestSurveyResponse(@RequestBody SurveyResponse surveyResponse, HttpSession session) {
        if (surveyResponse != null) {
            session.setAttribute("비회원 응답", surveyResponse);
            System.out.println("세션에 SurveyResponse 저장 완료: " + surveyResponse);
        } else {
            System.out.println("SurveyResponse가 null입니다.");
        }
        return ResponseEntity.ok("비회원 응답 저장 성공");
    }

}
