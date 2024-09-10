package jeonginho.perfume_recommend.controller.wishlist;

import jeonginho.perfume_recommend.Entity.Wishlist;
import jeonginho.perfume_recommend.dto.wishlist.WishlistResponse;
import jeonginho.perfume_recommend.service.wishlist.WishlistService;
import jeonginho.perfume_recommend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private JwtUtil jwtUtil;

    // 유저의 찜 목록 조회 API
    @GetMapping
    public List<WishlistResponse> getWishlist(@RequestHeader("Authorization") String token) {
        String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
        String userId = jwtUtil.extractUserId(tokenWithoutBearer);  // JWT에서 유저 ID 추출
        return wishlistService.getWishlistByUserId(userId);
    }


    // 유저의 찜 목록에 향수 추가 API
    @PostMapping
    public Wishlist addWishlist(@RequestHeader("Authorization") String token, @RequestParam String perfumeId) {
        String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
        String userId = jwtUtil.extractUserId(tokenWithoutBearer);  // JWT에서 유저 ID 추출
        return wishlistService.addWishlist(userId, perfumeId);
    }

    // 유저의 찜 목록에서 찜한 향수 삭제 API
    @DeleteMapping("/remove")
    public void removeWishlist(@RequestHeader("Authorization") String token, @RequestParam String perfumeId) {
        String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
        String userId = jwtUtil.extractUserId(tokenWithoutBearer);  // JWT에서 유저 ID 추출
        wishlistService.removeWishlist(userId, perfumeId);
    }
}
