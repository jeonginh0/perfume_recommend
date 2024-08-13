package jeonginho.perfume_recommend.repository.user;

import jeonginho.perfume_recommend.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    String findByNickname(String nickname);
    String findByPassword(String password);
    String findByPhoneNumber(String phoneNumber);
}
