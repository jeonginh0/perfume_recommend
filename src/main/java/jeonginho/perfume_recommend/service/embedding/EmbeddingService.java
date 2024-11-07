package jeonginho.perfume_recommend.service.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jeonginho.perfume_recommend.Entity.embedding.Embedding;
import jeonginho.perfume_recommend.dto.embedding.EmbeddingResponse;
import jeonginho.perfume_recommend.Entity.perfume.Perfume;
import jeonginho.perfume_recommend.dto.perfume.PerfumeRequest;
import jeonginho.perfume_recommend.repository.embedding.EmbeddingRepository;
import jeonginho.perfume_recommend.repository.perfume.PerfumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
public class EmbeddingService {
    @Autowired
    private EmbeddingRepository embeddingRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    public Iterable<Embedding> getAllEmbeddings() {
        return embeddingRepository.findAll();
    }

    public Optional<Embedding> getEmbeddingById(String id) {
        return embeddingRepository.findById(id);
    }

    public void saveEmbedding(Embedding embedding) {
        embeddingRepository.save(embedding);
    }

    public void generateAndSaveAllEmbeddings() {
        List<Perfume> allPerfumes = perfumeRepository.findAll();

        for (Perfume perfume : allPerfumes) {
            // Check if the embedding already exists
            if (!embeddingRepository.findByPerfumeId(perfume.getId()).isPresent()) {
                // Generate and save embedding
                List<Double> embeddingValue = generateEmbedding(perfume);
                Embedding embedding = Embedding.builder()
                        .perfumeId(perfume.getId())
                        .embedding(embeddingValue)
                        .build();
                saveEmbedding(embedding);
            }
        }
    }

    private List<Double> generateEmbedding(Perfume perfume) {
        String genders = String.join(", ", perfume.getGender());
        String seasons = String.join(", ", perfume.getSeason());
        String accords = String.join(", ", perfume.getAcode());
        String situations = String.join(", ", perfume.getSituation());
        String duration = perfume.getDuration();  // 추가된 duration 필드
        String price = perfume.getPrice();

        // Prompt 문자열 생성
        String prompt = String.format(
                "다음 향수의 정보를 기반으로 임베딩을 생성해줘:\n" +
                        "브랜드: %s\n" +
                        "이름: %s\n" +
                        "주요 어코드: %s\n" +
                        "성별: %s\n" +
                        "계절: %s\n" +
                        "상황: %s\n" +
                        "농도: %s\n" +
                        "가격: %s\n",
                perfume.getBrand(),
                perfume.getPerfume(),
                accords,
                genders,
                seasons,
                situations,
                duration,
                price
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;

        try {
            requestBody = objectMapper.writeValueAsString(
                    Map.of(
                            "model", "text-embedding-ada-002",
                            "input", prompt
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to create request body", e);
        }

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://api.openai.com/v1/embeddings", entity, String.class);

        String responseBody = response.getBody();
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                System.out.println("API Response: " + responseBody); // 응답 출력
                EmbeddingResponse embeddingResponse = objectMapper.readValue(responseBody, EmbeddingResponse.class);
                return embeddingResponse.getData().get(0).getEmbedding();
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse embedding response", e);
            }
        } else {
            System.err.println("Error Response: " + responseBody); // 오류 응답 로그
            throw new RuntimeException("Failed to generate embedding: " + response.getStatusCode());
        }
    }

    public List<Double> generateUserEmbedding(String userInput) {
        String prompt = "다음 요구 사항을 기반으로 향수 임베딩을 생성해줘:\n" +
                "요구 사항: " + userInput + "\n";

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;

        try {
            requestBody = objectMapper.writeValueAsString(
                    Map.of(
                            "model", "text-embedding-ada-002",
                            "input", prompt
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to create request body", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://api.openai.com/v1/embeddings", entity, String.class);

        String responseBody = response.getBody();
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                EmbeddingResponse embeddingResponse = objectMapper.readValue(responseBody, EmbeddingResponse.class);
                return embeddingResponse.getData().get(0).getEmbedding();
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse embedding response", e);
            }
        } else {
            System.err.println("Error Response: " + responseBody);
            throw new RuntimeException("Failed to generate embedding: " + response.getStatusCode());
        }
    }


    public double calculateSimilarity(List<Double> userEmbedding, List<Double> perfumeEmbedding) {
        double dotProduct = 0.0;
        double normUser = 0.0;
        double normPerfume = 0.0;

        for (int i = 0; i < userEmbedding.size(); i++) {
            double userValue = userEmbedding.get(i);
            double perfumeValue = perfumeEmbedding.get(i);

            dotProduct += userValue * perfumeValue;
            normUser += Math.pow(userValue, 2);
            normPerfume += Math.pow(perfumeValue, 2);
        }

        normUser = Math.sqrt(normUser);
        normPerfume = Math.sqrt(normPerfume);

        return dotProduct / (normUser * normPerfume);
    }

    public String generateRecommendationReason(List<Perfume> perfumes, String userInput) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("다음 향수에 대해 각 향수에 대한 추천 이유를 3줄 이내로 설명해줘 순위는 매길 필요 없어.\n")
                .append("또한 이유를 출력할 때 높임말을 사용하고, 주관적으로 확신을 가지고 이유를 생성해야 해. 특히, 사용자가 입력한 부분에 초첨을 두고 향에 대해 잘 알지 못하는 사람도 쉽게 이해할 수 있도록 이유를 생성해야 해. 또한 어울리는 스타일과 어떤 분위기의 사람이 쓰면 좋은지도 알려줘.");
        queryBuilder.append(userInput).append("\n\n");

        for (int i = 0; i < perfumes.size(); i++) {
            Perfume perfume = perfumes.get(i);
            queryBuilder.append(String.format(
                    "%d. %s, 주요 어코드: %s, 성별: %s, 계절: %s, 상황: %s, 농도: %s, 향수설명: %s\n",
                    i + 1,  // 향수 번호 추가
                    perfume.getPerfume(),
                    String.join(", ", perfume.getAcode()),
                    String.join(", ", perfume.getGender()),
                    String.join(", ", perfume.getSeason()),
                    String.join(", ", perfume.getSituation()),
                    perfume.getDuration(),
                    perfume.getDescription()
            ));
        }

        // 프롬프트 출력
        System.out.println("Generated Prompt: " + queryBuilder.toString());

        String query = queryBuilder.toString().replaceAll("\n", "\\n").replaceAll("\"", "\\\"");

        String requestBody;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            requestBody = objectMapper.writeValueAsString(
                    Map.of(
                            "model", "gpt-4",
                            "messages", List.of(Map.of("role", "user", "content", query)),
                            "max_tokens", 512
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to create request body", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://api.openai.com/v1/chat/completions", entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(response.getBody());

                // API 응답 로그
                System.out.println("API Response: " + response.getBody());

                String recommendationText = responseJson.get("choices").get(0).get("message").get("content").asText();

                // JSON 형식으로 변환
                ObjectMapper jsonMapper = new ObjectMapper();
                Map<String, String> recommendationMap = new LinkedHashMap<>();

                // 응답에서 각 향수 번호별 추천 이유 추출
                String[] lines = recommendationText.split("\\n");
                for (int i = 0; i < perfumes.size(); i++) {
                    String perfumeName = perfumes.get(i).getPerfume();
                    for (String line : lines) {
                        if (line.startsWith((i + 1) + ". ")) {
                            // 번호로 시작하는 줄을 찾아 향수 이름과 이유를 추출
                            String reason = line.substring(line.indexOf(":") + 1).trim();
                            recommendationMap.put(perfumeName, reason);
                            break;  // 해당 향수에 대한 이유를 찾았으므로 다음 향수로 넘어감
                        }
                    }
                }

                return jsonMapper.writeValueAsString(recommendationMap);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse recommendation reason response", e);
            }
        } else {
            System.err.println("응답 오류 발생: " + response.getBody());
            throw new RuntimeException("향수 추천 이유 생성 실패: " + response.getStatusCode());
        }
    }

}
