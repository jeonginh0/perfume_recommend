package jeonginho.perfume_recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.security.SecureRandom;
import java.util.Base64;

@EnableMongoRepositories
@SpringBootApplication
public class PerfumeRecommendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerfumeRecommendApplication.class, args);

        /* 랜덤 키 만들기
        byte[] bytes = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);

        String secretKey = Base64.getEncoder().encodeToString(bytes);
        System.out.println("Generated Secret Key: " + secretKey);*/
    }

}
