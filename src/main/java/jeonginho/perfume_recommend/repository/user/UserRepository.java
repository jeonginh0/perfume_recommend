package jeonginho.perfume_recommend.repository.user;

import jeonginho.perfume_recommend.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByNickname(String nickname);
    String getUserNameById(String nickname);
    String findByPassword(String password);
}
