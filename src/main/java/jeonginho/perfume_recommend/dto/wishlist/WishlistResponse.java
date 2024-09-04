package jeonginho.perfume_recommend.dto.wishlist;

import jeonginho.perfume_recommend.Entity.Perfume;
import jeonginho.perfume_recommend.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WishlistResponse {
    private String id;
    private String userId;
    private String perfumeId;
    private Perfume perfume;
    private User user;
}
