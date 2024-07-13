package jeonginho.perfume_recommend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "perfumes")
public class Perfume {
    @Id
    private String id;

    private String brand; //브랜드
    private String perfume; //향수이름
    private String duration; //지속시간

    private List<String> acode; //어코드

    private String singlenote; //싱글노트
    private String topnote; //탑노트
    private String middlenote; //미들노트
    private String basenote; //베이스노트

    private String description; //설명

    private List<String> ml; //용량
    private String image; //이미지 URL
    private String pageurl; //향수 상세 페이지 URL

}
