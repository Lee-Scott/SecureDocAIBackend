package com.familyFirstSoftware.SecureDocAIBackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpResponse<T> {
    protected LocalDateTime timeStamp;
    protected int statusCode;
    protected String message;
    protected T data;
    protected Map<String, String> errors;

        public static <T> HttpResponse<T> ok(T data, String message) {
        return new HttpResponse<>(LocalDateTime.now(), 200, message, data, null);
    }

        public static <T> HttpResponse<T> created(T data, String message) {
        return new HttpResponse<>(LocalDateTime.now(), 201, message, data, null);
    }

        public static <T> HttpResponse<T> error(String message, Map<String, String> errors) {
        return new HttpResponse<>(LocalDateTime.now(), 400, message, null, errors);
    }
}
