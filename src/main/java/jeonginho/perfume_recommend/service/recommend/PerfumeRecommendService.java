package jeonginho.perfume_recommend.service.recommend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jeonginho.perfume_recommend.Entity.embedding.Embedding;
import jeonginho.perfume_recommend.Entity.perfume.Perfume;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import jeonginho.perfume_recommend.Entity.recommend.RecommendedPerfume;
import jeonginho.perfume_recommend.repository.embedding.EmbeddingRepository;
import jeonginho.perfume_recommend.repository.perfume.PerfumeRepository;
import jeonginho.perfume_recommend.repository.recommend.RecommendedPerfumeRepository;
import jeonginho.perfume_recommend.service.embedding.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerfumeRecommendService {
    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private EmbeddingRepository embeddingRepository;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private RecommendedPerfumeRepository recommendedPerfumeRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // JWT를 사용하여 회원 구분

    public Iterable<Perfume> getAllPerfumes() {
        return perfumeRepository.findAll();
    }

    public void savePerfume(Perfume perfume) {
        perfumeRepository.save(perfume);
    }

    public List<RecommendedPerfume> recommendPerfumesUsingEmbedding(String userInput, HttpServletRequest request, HttpSession session) {
        // JWT에서 userId 추출
        String userId = getUserIdFromRequest(request);

        // 사용자 임베딩 생성
        List<Double> userEmbedding = embeddingService.generateUserEmbedding(userInput);

        // 조건 추출
        List<String> seasons = extractSeasonsFromUserInput(userInput);
        List<String> genders = extractGendersFromUserInput(userInput);
        List<String> durations = extractDurationFromUserInput(userInput);

        List<Perfume> filteredPerfumes = perfumeRepository.findAll().stream()
                .filter(perfume -> (seasons.isEmpty() || perfume.getSeason().stream().anyMatch(seasons::contains)))
                .filter(perfume -> (genders.isEmpty() || perfume.getGender().stream().anyMatch(genders::contains)))
                .filter(perfume -> (durations.isEmpty() || durations.contains(perfume.getDuration())))
                .collect(Collectors.toList());


        // 유사도 계산
        List<PerfumeWithSimilarity> perfumeWithSimilarities = filteredPerfumes.stream()
                .map(perfume -> {
                    Optional<Embedding> embeddingOptional = embeddingRepository.findByPerfumeId(perfume.getId());
                    if (embeddingOptional.isPresent()) {
                        List<Double> perfumeEmbedding = embeddingOptional.get().getEmbedding();
                        double similarity = embeddingService.calculateSimilarity(userEmbedding, perfumeEmbedding);
                        return new PerfumeWithSimilarity(perfume, similarity);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(PerfumeWithSimilarity::getSimilarity).reversed())
                .limit(3)
                .collect(Collectors.toList());

        // 추천 리스트 생성
        List<Perfume> result = perfumeWithSimilarities.stream()
                .map(PerfumeWithSimilarity::getPerfume)
                .collect(Collectors.toList());

        // 추천 이유 생성
        try {
            String recommendationReasonJson = embeddingService.generateRecommendationReason(result, userInput);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode recommendationReasons = objectMapper.readTree(recommendationReasonJson);

            List<RecommendedPerfume.PerfumeRecommendation> perfumeRecommendations = new ArrayList<>();
            for (Perfume perfume : result) {
                if (recommendationReasons.has(perfume.getPerfume())) {
                    String recommendationReason = recommendationReasons.get(perfume.getPerfume()).asText();
                    perfumeRecommendations.add(RecommendedPerfume.PerfumeRecommendation.builder()
                            .perfumeId(perfume.getId())
                            .recommendationReason(recommendationReason)
                            .build());
                }
            }

            if (userId != null) { // 회원인 경우
                RecommendedPerfume recommendedPerfume = RecommendedPerfume.builder()
                        .userId(userId)
                        .perfumeRecommendations(perfumeRecommendations)
                        .build();
                recommendedPerfumeRepository.save(recommendedPerfume);
                return List.of(recommendedPerfume);
            } else { // 비회원인 경우 세션에 저장
                session.setAttribute("nonMemberRecommendations", perfumeRecommendations);
                return Collections.emptyList();
            }

        } catch (Exception e) {
            System.err.println("추천 이유 생성 실패: " + e.getMessage());
            return List.of();
        }
    }

    // 회원 추천 기록 조회
    public List<RecommendedPerfume> getMemberRecommendations(HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        if (userId != null) {
            return recommendedPerfumeRepository.findByUserId(userId);
        }
        return Collections.emptyList();
    }

    // 비회원 추천 기록 조회
    public List<RecommendedPerfume.PerfumeRecommendation> getNonMemberRecommendations(HttpSession session) {
        List<RecommendedPerfume.PerfumeRecommendation> recommendations = (List<RecommendedPerfume.PerfumeRecommendation>) session.getAttribute("nonMemberRecommendations");
        return recommendations != null ? recommendations : Collections.emptyList();
    }

    // JWT에서 userId를 추출하는 메서드
    private String getUserIdFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // "Bearer " 제거
            if (jwtTokenProvider.validateToken(token)) {
                return jwtTokenProvider.getUserIdFromJWT(token); // JWT에서 userId 추출
            }
        }
        return null; // 비회원인 경우
    }

    private List<String> extractSeasonsFromUserInput(String userInput) {
        List<String> seasons = new ArrayList<>();
        if (userInput.contains("여름")) seasons.add("Summer");
        if (userInput.contains("봄")) seasons.add("Spring");
        if (userInput.contains("가을")) seasons.add("Fall");
        if (userInput.contains("겨울")) seasons.add("Winter");
        return seasons;
    }

    private List<String> extractGendersFromUserInput(String userInput) {
        List<String> genders = new ArrayList<>();
        if (userInput.contains("남성") || userInput.contains("남자")) genders.add("Man");
        if (userInput.contains("여성") || userInput.contains("여자")) genders.add("Woman");
        if (userInput.contains("남녀공용") || userInput.contains("중성")) genders.add("Unisex");
        return genders;
    }

    private List<String> extractDurationFromUserInput(String userInput) {
        if (userInput.contains("오래 지속")) {
            return List.of("퍼퓸", "오 드 퍼퓸"); // 지속 시간이 긴 향수
        } else if (userInput.contains("짧게 지속")) {
            return List.of("오 드 뚜왈렛", "오 드 코롱"); // 지속 시간이 짧은 향수
        }
        return List.of("오 드 뚜왈렛", "오 드 퍼퓸", "퍼퓸", "오 드 코롱"); // 특정 지속 시간 없이 모두 포함
    }


    // 향수와 유사도를 담는 클래스
    private static class PerfumeWithSimilarity {
        private final Perfume perfume;
        private final double similarity;

        public PerfumeWithSimilarity(Perfume perfume, double similarity) {
            this.perfume = perfume;
            this.similarity = similarity;
        }

        public Perfume getPerfume() {
            return perfume;
        }

        public double getSimilarity() {
            return similarity;
        }
    }
}