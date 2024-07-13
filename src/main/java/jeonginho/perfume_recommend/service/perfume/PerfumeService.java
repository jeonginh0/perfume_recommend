package jeonginho.perfume_recommend.service.perfume;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeonginho.perfume_recommend.model.Perfume;
import jeonginho.perfume_recommend.repository.Perfume.PerfumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PerfumeService {

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /*
    * importPerfumesJson
    * param : String filePath (JSON 파일의 경로)
    * */
    public void importPerfumesJson(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath); //리소스를 로드하기 위한 클래스 객체 선언
        InputStream inputStream = resource.getInputStream(); //파일로부터 데이터를 읽어오기 위한 객체 선언

        List<Perfume> perfumes = objectMapper.readValue(inputStream,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Perfume.class));

        perfumeRepository.saveAll(perfumes); // Perfume list 객체들을 DB에 저장
    }

    public List<Perfume> recommendPerfumesByCriteria(String duration, List<String> acode) {
        // duration과 acode를 기반으로 향수를 조회하여 랜덤으로 5개 추천
        // duration이 일치하고 acode가 포함되는 향수들을 데이터베이스에서 조회
        List<Perfume> matchingPerfumes = perfumeRepository.findByDurationAndAcodeIn(duration, acode);

        // 랜덤으로 5개 향수 선택
        List<Perfume> recommendedPerfumes = new ArrayList<>();
        Random random = new Random();
        int size = matchingPerfumes.size();
        int limit = Math.min(size, 10); // 매칭된 향수가 10개보다 적을 수도 있음

        for (int i = 0; i < limit; i++) {
            int randomIndex = random.nextInt(size);
            recommendedPerfumes.add(matchingPerfumes.get(randomIndex));
        }
        System.out.println("추천된 향수 정보: " + recommendedPerfumes);
        return recommendedPerfumes;
    }
}
