package mayur.dev.smartexpensetackerapi.expense.controller;

import mayur.dev.smartexpensetackerapi.core.utils.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {
    @GetMapping("/test")
    public ApiResponse<Map<String, Object>> test() {

        Map<String, Object> testData = Map.of(
                "status", "server_up",
                "code", 200
        );

        return ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Test endpoint reached successfully!")
                .data(testData)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/hello")
    public String hello() {
        return "You are authenticated!";
    }
}
