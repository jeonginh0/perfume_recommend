package jeonginho.perfume_recommend.dto.user.preference;

import lombok.Data;

import java.util.List;

@Data
public class UserPreferencesDto {
    private List<String> preferenceNote;
    private List<String> preferenceDuration;
    private List<String> preferenceSeason;
    private List<String> preferenceSituation;
}