package jeonginho.perfume_recommend.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor  // 기본 생성자 추가
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
}
