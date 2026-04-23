package mayur.dev.smartexpensetackerapi.features.healthCheck;

import mayur.dev.smartexpensetackerapi.core.utils.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthCheckController {
    @GetMapping("/test")
    public ApiResponse<Map<String, Object>> test() {

        Map<String, Object> data = Map.of(
                "status", "server_up",
                "code", 200
        );
        return ApiResponse.success("Test endpoint reached successfully", data);

    }

    @GetMapping("/hello")
    public String hello() {
        return "You are authenticated!";
    }
}
