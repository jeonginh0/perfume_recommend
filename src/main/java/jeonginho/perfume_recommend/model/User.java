package jeonginho.perfume_recommend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String nickname; //사용자 닉네임
    private String email; //이메일
    private String password; //비밀번호
    private String phoneNumber; //휴대폰번호

    private String preferenceAcode; //사용자가 선호하는 향기 코드
    private String preferenceSeason; //사용자가 선호하는 계절

    private LocalDateTime createdAt; //계정 생성일자
}

