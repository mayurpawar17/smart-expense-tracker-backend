package mayur.dev.smartexpensetackerapi.controller;

import mayur.dev.smartexpensetackerapi.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
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
}
