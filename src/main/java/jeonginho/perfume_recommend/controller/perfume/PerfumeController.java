package jeonginho.perfume_recommend.controller.perfume;

import jeonginho.perfume_recommend.model.Perfume;
import jeonginho.perfume_recommend.repository.Perfume.PerfumeRepository;
import jeonginho.perfume_recommend.service.perfume.PerfumeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/perfumes")
public class PerfumeController {

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
            // Update fields here
            return perfumeRepository.save(perfume);
        } else {
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public void deletePerfume(@PathVariable String id) {
        perfumeRepository.deleteById(id);
    }

    // json 데이터를 디비에 저장하기 위한 메서드. 프로젝트 최초 1회만 사용.
    @PostMapping("/importData")
    public void importData() throws IOException {
        perfumeService.importPerfumesJson(jsonFilePath);
    }

    // 향수 추천 컨트롤러 수정
    @GetMapping("/recommend")
    public ResponseEntity<List<Perfume>> recommendPerfumes(@RequestParam String userId) {
        List<Perfume> recommendedPerfumes = perfumeService.recommendPerfumeByUser(userId);
        return ResponseEntity.ok(recommendedPerfumes);
    }
}
