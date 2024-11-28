package jeonginho.perfume_recommend.dto.wishlist;

import jeonginho.perfume_recommend.Entity.perfume.Perfume;
import jeonginho.perfume_recommend.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WishlistResponse {
    private String userId;
    private List<String> perfumeIds;
    private List<Perfume> perfumes;
    private User user;
}
