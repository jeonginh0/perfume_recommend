package jeonginho.perfume_recommend.controller.survey;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jeonginho.perfume_recommend.Entity.survey.SurveyResponse;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import jeonginho.perfume_recommend.repository.Survey.SurveyResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/survey")
public class SurveyController {

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 회원 응답 저장 API
    @PostMapping("/response/member")
    public ResponseEntity<String> saveMemberSurveyResponse(@RequestBody SurveyResponse surveyResponse, HttpServletRequest request) {
        // JWT 토큰 추출
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // "Bearer " 제거
        } else {
            return ResponseEntity.badRequest().body("JWT 토큰이 필요합니다.");
        }

        // userId 추출
        String userId;
        try {
            userId = jwtTokenProvider.getUserIdFromJWT(token);
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("유효하지 않은 JWT 토큰입니다.");
        }

        // userId가 존재하는지 확인
        if (userId == null) {
            return ResponseEntity.badRequest().body("회원 ID 필요.");
        }

        // 응답 저장
        surveyResponse.setUserId(userId);
        surveyResponseRepository.save(surveyResponse);
        return ResponseEntity.ok("회원 설문 응답 저장완료.");
    }

    // 비회원(게스트) 응답 세션 저장 API
    @PostMapping("/response/guest")
    public ResponseEntity<String> saveGuestSurveyResponse(@RequestBody SurveyResponse surveyResponse, HttpSession session) {
        session.setAttribute("guestSurveyResponse", surveyResponse);
        surveyResponseRepository.save(surveyResponse);
        System.out.println("게스트 응답: " + surveyResponse);

        return ResponseEntity.ok(surveyResponse.getId());
    }
}