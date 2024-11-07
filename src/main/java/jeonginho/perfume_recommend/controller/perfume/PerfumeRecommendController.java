package jeonginho.perfume_recommend.controller.perfume;

import jakarta.servlet.http.HttpServletRequest;
import jeonginho.perfume_recommend.Entity.perfume.Perfume;
import jeonginho.perfume_recommend.Entity.recommend.RecommendedPerfume;
import jeonginho.perfume_recommend.service.perfume.PerfumeRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfumes")
public class PerfumeRecommendController {

    @Autowired
    private PerfumeRecommendService perfumeRecommendService;

    @GetMapping("/all")
    public Iterable<Perfume> getAllPerfumes() {
        return perfumeRecommendService.getAllPerfumes();
    }

    @PostMapping("/save")
    public String savePerfume(@RequestBody Perfume perfume) {
        perfumeRecommendService.savePerfume(perfume);
        return "향수 저장 완료";
    }

    @PostMapping("/recommend")
    public List<RecommendedPerfume> recommendPerfumes(@RequestBody String userInput, HttpServletRequest request) {
        return perfumeRecommendService.recommendPerfumesUsingEmbedding(userInput, request);
    }
}
