package com.proten.weeklyreport.controller;

import com.proten.weeklyreport.dto.WeeklyReportRequest;
import com.proten.weeklyreport.dto.WeeklyReportResponse;
import com.proten.weeklyreport.service.WeeklyReportService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 웹(HTTP) 계층.
 *
 * <p>역할: URL/HTTP 메서드 매핑, 요청 파라미터·바디 바인딩, 검증 트리거(@Valid),
 * 상태 코드 결정. 실제 처리 로직은 Service 에 위임한다(컨트롤러는 얇게 유지).</p>
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final WeeklyReportService service;

    /** 등록: 201 Created + Location 헤더 */
    @PostMapping
    public ResponseEntity<WeeklyReportResponse> create(@Valid @RequestBody WeeklyReportRequest request) {
        WeeklyReportResponse created = service.create(request);
        URI location = URI.create("/api/reports/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /** 목록 조회: 부서/성명/기간(from~to) 으로 선택 필터링 */
    @GetMapping
    public ResponseEntity<List<WeeklyReportResponse>> findAll(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String name,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.findAll(department, name, from, to));
    }

    /** 단건 조회: 없으면 404 (GlobalExceptionHandler 처리) */
    @GetMapping("/{id}")
    public ResponseEntity<WeeklyReportResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /** 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<WeeklyReportResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody WeeklyReportRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    /** 삭제: 204 No Content */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
