package jeonginho.perfume_recommend.Entity.note;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "situation_notes")
public class SituationNote {

    @Id
    private String id;
    private String situation;
    private List<String> notes;
}
