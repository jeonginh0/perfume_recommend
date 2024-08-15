package jeonginho.perfume_recommend.service.user;

import jeonginho.perfume_recommend.Entity.recommend.RecommendPerfumes;
import jeonginho.perfume_recommend.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChangeInfoService {

    @Autowired
    UserRepository userRepository;
}
