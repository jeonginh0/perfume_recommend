package jeonginho.perfume_recommend.repository.perfume;

import jeonginho.perfume_recommend.Entity.Perfume;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PerfumeRepository extends MongoRepository<Perfume, String> {
    List<Perfume> findByTopnoteContaining(String note);
    List<Perfume> findByMiddlenoteContaining(String note);
    List<Perfume> findByBasenoteContaining(String note);
    List<Perfume> findBySinglenoteContaining(String note);

    List<Perfume> findByDurationContaining(String duration);
    List<Perfume> findByBrandContaining(String brand);

    // 특정 브랜드 목록과 지속시간 목록에 해당하는 향수들을 조회하는 메서드
    List<Perfume> findByBrandInIgnoreCaseAndDurationInIgnoreCase(List<String> brands, List<String> durations);

    // 브랜드 목록에 해당하는 향수 조회 (대소문자 구분하지 않음)
    List<Perfume> findByBrandInIgnoreCase(List<String> brands);

    // 지속시간 목록에 해당하는 향수 조회 (대소문자 구분하지 않음)
    List<Perfume> findByDurationInIgnoreCase(List<String> durations);

}
