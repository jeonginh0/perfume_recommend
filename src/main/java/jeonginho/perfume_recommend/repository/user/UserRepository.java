package jeonginho.perfume_recommend.repository.user;

import jeonginho.perfume_recommend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
