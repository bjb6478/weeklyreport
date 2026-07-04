package com.proten.weeklyreport.exception;

/**
 * 요청한 리소스를 찾지 못했을 때 던지는 예외.
 * GlobalExceptionHandler 가 이 예외를 잡아 HTTP 404 로 변환한다.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
