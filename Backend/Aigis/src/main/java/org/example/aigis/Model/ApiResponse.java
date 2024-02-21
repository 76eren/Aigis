package org.example.aigis.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;


public class ApiResponse<T> extends ResponseEntity<ResponsePayload<T>> {
    public ApiResponse(String message) {
        super(new ResponsePayload<>(message), HttpStatus.OK);
    }

    public ApiResponse(T payload) {
        super(new ResponsePayload<>(payload), HttpStatus.OK);
    }

    public ApiResponse(T payload, HttpStatusCode statusCode) {
        super(new ResponsePayload<>(payload), statusCode);
    }

    public ApiResponse(String message, HttpStatusCode statusCode) {
        super(new ResponsePayload<>(message), statusCode);
    }

    public ApiResponse(T payload, String message, HttpStatusCode statusCode) {
        super(new ResponsePayload<>(payload, message), statusCode);
    }
}

@Data
@Builder
@AllArgsConstructor
class ResponsePayload<T> {
    private T payload;
    private String message;

    public ResponsePayload(T payload) {
        this.payload = payload;
    }
    public ResponsePayload(String message) {
        this.message = message;
    }
}
