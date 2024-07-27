package jeonginho.perfume_recommend.model.note;

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
@Document(collection = "season_notes")
public class SeasonNote {

    @Id
    private String id;
    private String season;
    private List<String> notes;
}
