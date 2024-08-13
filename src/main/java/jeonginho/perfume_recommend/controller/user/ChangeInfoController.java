package jeonginho.perfume_recommend.controller.user;

import jeonginho.perfume_recommend.service.user.ChangeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/users")
public class ChangeInfoController {

    @Autowired
    ChangeInfoService changeInfoService;

    @PostMapping("/change/nickname")
    public String changeUserNickname (String nickname) {

    }
}
