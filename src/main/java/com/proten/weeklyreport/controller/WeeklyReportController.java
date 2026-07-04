package com.proten.weeklyreport.controller;

import com.proten.weeklyreport.dto.WeeklyReportRequest;
import com.proten.weeklyreport.dto.WeeklyReportResponse;
import com.proten.weeklyreport.service.WeeklyReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "주간보고", description = "주간보고 등록/조회/수정/삭제 API")
public class WeeklyReportController {

    private final WeeklyReportService service;

    /** 등록: 201 Created + Location 헤더 */
    @Operation(summary = "주간보고 등록", description = "새 주간보고를 등록한다. 부서/성명/기간은 필수.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "필수값 누락 등 검증 실패")
    })
    @PostMapping
    public ResponseEntity<WeeklyReportResponse> create(@Valid @RequestBody WeeklyReportRequest request) {
        WeeklyReportResponse created = service.create(request);
        URI location = URI.create("/api/reports/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /** 목록 조회: 부서/성명/기간(from~to) 으로 선택 필터링 */
    @Operation(summary = "주간보고 목록 조회",
            description = "부서/성명/기간(from~to)으로 선택 필터링. 파라미터 없으면 전체 조회(최신순).")
    @GetMapping
    public ResponseEntity<List<WeeklyReportResponse>> findAll(
            @Parameter(description = "부서 (완전일치)") @RequestParam(required = false) String department,
            @Parameter(description = "성명 (완전일치)") @RequestParam(required = false) String name,
            @Parameter(description = "기간 시작 (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "기간 종료 (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.findAll(department, name, from, to));
    }

    /** 단건 조회: 없으면 404 (GlobalExceptionHandler 처리) */
    @Operation(summary = "주간보고 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 id 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<WeeklyReportResponse> findById(
            @Parameter(description = "주간보고 id") @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /** 수정 */
    @Operation(summary = "주간보고 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "404", description = "해당 id 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<WeeklyReportResponse> update(
            @Parameter(description = "주간보고 id") @PathVariable Long id,
            @Valid @RequestBody WeeklyReportRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    /** 삭제: 204 No Content */
    @Operation(summary = "주간보고 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 id 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "주간보고 id") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
