package com.proten.weeklyreport.exception;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * 에러 응답 공통 포맷. 클라이언트가 일관된 형태로 오류를 파싱할 수 있게 한다.
 */
@Getter
@Builder
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    /** 필드 검증 실패 시 필드별 메시지 (검증 오류가 아닐 땐 null) */
    private Map<String, String> fieldErrors;
}
