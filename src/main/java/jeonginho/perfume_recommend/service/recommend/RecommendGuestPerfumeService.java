package jeonginho.perfume_recommend.service.recommend;

import jeonginho.perfume_recommend.Entity.perfume.Perfume;
import jeonginho.perfume_recommend.Entity.recommend.RecommendGuestPerfumes;
import jeonginho.perfume_recommend.repository.perfume.PerfumeRepository;
import jeonginho.perfume_recommend.repository.recommend.RecommendGuestPerfumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendGuestPerfumeService {

    @Autowired
    private RecommendGuestPerfumeRepository recommendGuestPerfumeRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    // 설문 응답으로 추천받은 향수 DB 저장 (게스트에 한하여)
    public void saveRecommendPerfumes(String guestId, List<Perfume> recommendedPerfumes) {
        Optional<RecommendGuestPerfumes> existingRecommendOpt = recommendGuestPerfumeRepository.findByGuestId(guestId);

        if (existingRecommendOpt.isPresent()) {
            RecommendGuestPerfumes existingRecommend = existingRecommendOpt.get();
            existingRecommend.setPerfumeIds(
                    recommendedPerfumes.stream().map(Perfume::getId).collect(Collectors.toList())
            );
            recommendGuestPerfumeRepository.save(existingRecommend);
            System.out.println("기존 추천 데이터를 업데이트했습니다: " + existingRecommend);
        } else {
            RecommendGuestPerfumes newRecommend = RecommendGuestPerfumes.builder()
                    .guestId(guestId)
                    .perfumeIds(
                            recommendedPerfumes.stream().map(Perfume::getId).collect(Collectors.toList())
                    )
                    .build();
            recommendGuestPerfumeRepository.save(newRecommend);
            System.out.println("새로운 추천 데이터를 저장했습니다: " + newRecommend);
        }
    }

    // 게스트별 추천된 향수들의 상세 정보를 조회
    public List<Perfume> getRecommendPerfumesGuest(String guestId) {
        Optional<RecommendGuestPerfumes> recommendations = recommendGuestPerfumeRepository.findByGuestId(guestId);

        List<String> perfumeIds = recommendations.stream()
                .flatMap(recommendation -> recommendation.getPerfumeIds().stream())
                .collect(Collectors.toList());

        return perfumeRepository.findAllById(perfumeIds);
    }
}
