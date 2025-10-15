package major.tradingserver;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String message;
    private T data;
    private int status;

    public ApiResponse(String message, T data, int status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    // Getters and setters
}
