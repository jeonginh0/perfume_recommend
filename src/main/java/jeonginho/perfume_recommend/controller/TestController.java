package jeonginho.perfume_recommend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
*
* PerfumeRecommendController.java
* 프로젝트의 전반적인 Controller 클래스 파일
* api 구현예정
*
* */

@RestController // @RestController = @Controller + @ResponseBody
public class TestController {

    @GetMapping("/api/hello")
    public String test() {
        System.out.println("Received request at /api/hello"); // 데이터 송 수신 상태 확인을 위한 디버깅
        return "Hello, world!";
    }
}
