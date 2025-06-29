package hello.backendproject.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exception")
public class TestController {

    @GetMapping
    public ResponseEntity<ErrorResponse> test() {
        throw new RuntimeException("테스트");
    }
}
