package jeonginho.perfume_recommend.dto.perfume;

import lombok.Data;

import java.util.List;

@Data
public class PerfumeRequest {
    public List<String> gender;
    public List<String> season;
    public List<String> acode;
    public List<String> situation;
    public String duration;
}
