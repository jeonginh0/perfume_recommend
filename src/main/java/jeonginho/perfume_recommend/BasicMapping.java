package jeonginho.perfume_recommend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicMapping {

    @GetMapping("/") // 루트페이지 이동
    public String main() {
        return "index";
    }
}
