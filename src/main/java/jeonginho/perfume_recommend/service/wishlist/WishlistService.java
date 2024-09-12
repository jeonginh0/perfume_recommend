package jeonginho.perfume_recommend.service.wishlist;

import jeonginho.perfume_recommend.Entity.Perfume;
import jeonginho.perfume_recommend.Entity.User;
import jeonginho.perfume_recommend.Entity.Wishlist;
import jeonginho.perfume_recommend.dto.wishlist.WishlistResponse;
import jeonginho.perfume_recommend.repository.perfume.PerfumeRepository;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import jeonginho.perfume_recommend.repository.wishlist.WishlistRepository;
import jeonginho.perfume_recommend.config.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private JwtTokenProvider jwtTokenProvider;

    public WishlistResponse getWishlistByUserId(String token) {
        String userId = jwtTokenProvider.getUserIdFromJWT(token);

        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        List<String> perfumeIds = wishlists.stream()
                .flatMap(wishlist -> wishlist.getPerfumeIds().stream())
                .distinct()
                .collect(Collectors.toList());
        List<Perfume> perfumes = perfumeRepository.findAllById(perfumeIds);

        Optional<User> user = userRepository.findById(userId);

        return new WishlistResponse(
                userId,
                perfumeIds,
                perfumes,
                user.orElse(null)
        );
    }

    public Wishlist addWishlist(String token, String perfumeId) {
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .stream()
                .findFirst()
                .orElse(new Wishlist());

        if (wishlist.getPerfumeIds() == null) {
            wishlist.setPerfumeIds(new ArrayList<>());
        }

        if (wishlist.getPerfumeIds().contains(perfumeId)) {
            return wishlist;
        }

        wishlist.getPerfumeIds().add(perfumeId);
        wishlist.setUserId(userId);
        return wishlistRepository.save(wishlist);
    }

    public void removeWishlist(String token, String perfumeId) {
        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .stream()
                .findFirst()
                .orElse(null);

        if (wishlist != null && wishlist.getPerfumeIds() != null) {
            wishlist.getPerfumeIds().remove(perfumeId);
            if (wishlist.getPerfumeIds().isEmpty()) {
                wishlistRepository.delete(wishlist);
            } else {
                wishlistRepository.save(wishlist);
            }
        }
    }
}
