package jeonginho.perfume_recommend.controller.perfume;

import jakarta.servlet.http.HttpSession;
import jeonginho.perfume_recommend.Entity.Perfume;
import jeonginho.perfume_recommend.repository.perfume.PerfumeRepository;
import jeonginho.perfume_recommend.service.survey.SurveyService;
import jeonginho.perfume_recommend.service.perfume.PerfumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/perfumes")
public class PerfumeController {

    @Autowired
    private SurveyService surveyService;

    private final PerfumeRepository perfumeRepository;

    private final PerfumeService perfumeService;

    public PerfumeController(PerfumeRepository perfumeRepository, PerfumeService perfumeService) {
        this.perfumeRepository = perfumeRepository;
        this.perfumeService = perfumeService;
    }

    @Value("${json.file.path}")
    private String jsonFilePath;


    @GetMapping
    public List<Perfume> getAllPerfumes() {
        return perfumeRepository.findAll();
    }

    @PostMapping
    public Perfume createPerfume(@RequestBody Perfume perfume) {
        return perfumeRepository.save(perfume);
    }

    @GetMapping("/{id}")
    public Perfume getPerfumeById(@PathVariable String id) {
        return perfumeRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Perfume updatePerfume(@PathVariable String id, @RequestBody Perfume perfumeDetails) {
        Perfume perfume = perfumeRepository.findById(id).orElse(null);
        if (perfume != null) {
            return perfumeRepository.save(perfume);
        } else {
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public void deletePerfume(@PathVariable String id) {
        perfumeRepository.deleteById(id);
    }

    // json 데이터를 디비에 저장하기 위한 컨트롤러. 프로젝트 최초 1회만 사용.
    @PostMapping("/importData")
    public void importData() throws IOException {
        perfumeService.importPerfumesJson(jsonFilePath);
    }

    // 회원일 때 향수 추천
    @GetMapping("/recommend/member/{userId}")
    public List<Perfume> recommendForMember(@PathVariable String userId) {
        return surveyService.processSurveyAndRecommendForMember(userId);
    }

    // 비회원일 때 향수 추천
    @GetMapping("/recommend/guest")
    public List<Perfume> recommendForGuest(HttpSession session) {
        String guestSessionId = (String) session.getAttribute("guestSessionId");

        if (guestSessionId == null) {
            guestSessionId = session.getId();
            session.setAttribute("guestSessionId", guestSessionId);
        }

        return surveyService.processSurveyAndRecommendForGuest(guestSessionId);
    }
}
