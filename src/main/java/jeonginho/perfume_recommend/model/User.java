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
@Document(collection = "Users")
public class User {
    @Id
    private String id;

    private String name;
    private String email;
    private String password;

    @CreatedDate
    private LocalDateTime createdAt;
}

