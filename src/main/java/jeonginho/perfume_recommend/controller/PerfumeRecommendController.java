package jeonginho.perfume_recommend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // @RestController = @Controller + @ResponseBody
public class PerfumeRecommendController {

    @GetMapping("/api/hello")
    public String test() {
        System.out.println("Received request at /api/hello");
        return "Hello, world!";
    }
}
