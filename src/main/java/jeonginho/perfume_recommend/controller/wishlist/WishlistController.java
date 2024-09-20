package jeonginho.perfume_recommend.controller.wishlist;

import jeonginho.perfume_recommend.dto.wishlist.WishlistResponse;
import jeonginho.perfume_recommend.service.wishlist.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    // 찜 목록 조회 API
    @GetMapping
    public WishlistResponse getWishlist(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        return wishlistService.getWishlistByUserId(jwtToken);
    }

    // 찜 목록 추가 API
    @PostMapping
    public void addWishlist(@RequestHeader("Authorization") String token, @RequestParam String perfumeId) {
        String jwtToken = token.substring(7);
        wishlistService.addWishlist(jwtToken, perfumeId);
    }

    // 특정 찜 항목 삭제 API
    @PostMapping("/remove")
    public void removeWishlist(@RequestHeader("Authorization") String token, @RequestParam String perfumeId) {
        String jwtToken = token.substring(7);
        wishlistService.removeWishlist(jwtToken, perfumeId);
    }
}
