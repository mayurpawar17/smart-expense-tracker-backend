package mayur.dev.smartexpensetackerapi.core.utils.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({"status", "message", "data"})
@JsonInclude(JsonInclude.Include.NON_NULL) // Hides null fields (like pagination on single-object responses)
public class ApiResponse<T> {
    private boolean status;
    private String message;
    private T data;
    private Pagination pagination;


    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data, Pagination pagination) {
        return new ApiResponse<>(true, message, data, pagination);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }
}

