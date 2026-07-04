package com.proten.weeklyreport.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리기.
 *
 * <p>컨트롤러 어디서든 발생한 예외를 한곳에서 잡아 일관된 HTTP 응답으로 변환한다.
 *   - ResourceNotFoundException      -> 404 Not Found
 *   - MethodArgumentNotValidException -> 400 Bad Request (@Valid 검증 실패)</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 없는 id 조회/수정/삭제 시 404 */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /** @Valid 검증 실패 시 400 + 필드별 메시지 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("입력값 검증에 실패했습니다.")
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.badRequest().body(body);
    }
}
