package jeonginho.perfume_recommend.service.wishlist;

import jeonginho.perfume_recommend.Entity.Perfume;
import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.Entity.Wishlist;
import jeonginho.perfume_recommend.dto.wishlist.WishlistResponse;
import jeonginho.perfume_recommend.repository.perfume.PerfumeRepository;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import jeonginho.perfume_recommend.repository.wishlist.WishlistRepository;
import jeonginho.perfume_recommend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public List<WishlistResponse> getWishlistByUserId(String token) {
        String userId = jwtUtil.extractUserId(token);

        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);

        return wishlists.stream().map(wishlist -> {
            Optional<Perfume> perfume = perfumeRepository.findById(wishlist.getPerfumeId());
            Optional<User> user = userRepository.findById(wishlist.getUserId());
            return new WishlistResponse(
                    wishlist.getId(),
                    wishlist.getUserId(),
                    wishlist.getPerfumeId(),
                    perfume.orElse(null),
                    user.orElse(null)
            );
        }).collect(Collectors.toList());
    }

    public Wishlist addWishlist(String token, String perfumeId) {
        String userId = jwtUtil.extractUserId(token);
        Wishlist existingWishlist = wishlistRepository.findByUserIdAndPerfumeId(userId, perfumeId);

        if (existingWishlist != null) {
            return existingWishlist;
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setPerfumeId(perfumeId);
        return wishlistRepository.save(wishlist);
    }

    public void removeWishlist(String token, String perfumeId) {
        String userId = jwtUtil.extractUserId(token);
        Wishlist wishlist = wishlistRepository.findByUserIdAndPerfumeId(userId, perfumeId);

        if (wishlist != null) {
            wishlistRepository.delete(wishlist);
        }
    }
}
