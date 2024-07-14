package jeonginho.perfume_recommend.service.perfume;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeonginho.perfume_recommend.model.Perfume;
import jeonginho.perfume_recommend.model.User;
import jeonginho.perfume_recommend.repository.Perfume.PerfumeRepository;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PerfumeService {

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /*
    * importPerfumesJson
    * param : String filePath (JSON 파일의 경로)
    * 1. 프로젝트 내 최초 1회 호출
    * */
    public void importPerfumesJson(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath); //리소스를 로드하기 위한 클래스 객체 선언
        InputStream inputStream = resource.getInputStream(); //파일로부터 데이터를 읽어오기 위한 객체 선언

        List<Perfume> perfumes = objectMapper.readValue(inputStream,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Perfume.class));

        perfumeRepository.saveAll(perfumes); // Perfume list 객체들을 DB에 저장
    }

    public List<Perfume> recommendPerfumeByUser(String userId) {
        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<String> preferenceAcodes = user.getPreferenceAcode();
        String preferenceDuration = user.getPreferenceDuration();

        // preferenceAcode와 preferenceDuration을 기반으로 향수를 조회
        List<Perfume> matchingPerfumes = perfumeRepository.findByDurationAndAcodeIn(preferenceDuration, preferenceAcodes);

        // 향수를 랜덤하게 섞고 상위 5개 선택
        Collections.shuffle(matchingPerfumes);
        List<Perfume> recommendedPerfumes = matchingPerfumes.stream()
                .limit(5)
                .collect(Collectors.toList());

        System.out.println("추천된 향수 정보: " + recommendedPerfumes);
        return recommendedPerfumes;
    }

}
