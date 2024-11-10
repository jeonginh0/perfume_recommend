package jeonginho.perfume_recommend.controller.embedding;

import jeonginho.perfume_recommend.Entity.embedding.Embedding;
import jeonginho.perfume_recommend.service.embedding.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/embedding")
public class EmbeddingController {

    @Autowired
    private EmbeddingService embeddingService;

    @GetMapping("/all")
    public Iterable<Embedding> getAllEmbeddings() {
        return embeddingService.getAllEmbeddings();
    }

    @PostMapping("/generate-all")
    public String generateAndSaveAllEmbeddings() {
        embeddingService.generateAndSaveAllEmbeddings();
        return "모든 향수의 임베딩을 생성하고 저장하였습니다.";
    }
}