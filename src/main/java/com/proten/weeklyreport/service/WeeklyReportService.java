package com.proten.weeklyreport.service;

import com.proten.weeklyreport.dto.WeeklyReportRequest;
import com.proten.weeklyreport.dto.WeeklyReportResponse;
import com.proten.weeklyreport.entity.WeeklyReport;
import com.proten.weeklyreport.exception.ResourceNotFoundException;
import com.proten.weeklyreport.repository.WeeklyReportRepository;
import com.proten.weeklyreport.repository.WeeklyReportSpecification;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 비즈니스 로직 계층.
 *
 * <p>역할:
 *   - 트랜잭션 경계를 잡고(@Transactional),
 *   - Repository 를 호출해 엔티티를 다루고,
 *   - 없는 리소스면 예외를 던지고,
 *   - 엔티티 <-> DTO 변환을 조율한다.
 * 컨트롤러는 HTTP 관심사만, 여기는 "무엇을 하는지"에 집중한다.</p>
 */
@Service
@RequiredArgsConstructor // final 필드 생성자 주입
@Transactional(readOnly = true) // 기본은 읽기 전용, 쓰기 메서드에만 별도 지정
public class WeeklyReportService {

    private final WeeklyReportRepository repository;

    /** 등록 */
    @Transactional
    public WeeklyReportResponse create(WeeklyReportRequest request) {
        WeeklyReport saved = repository.save(request.toEntity());
        return WeeklyReportResponse.from(saved);
    }

    /** 목록 조회 (부서/성명/기간 필터, 최신 등록순 정렬) */
    public List<WeeklyReportResponse> findAll(String department, String name,
                                              LocalDate from, LocalDate to) {
        return repository
                .findAll(WeeklyReportSpecification.filter(department, name, from, to),
                        Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(WeeklyReportResponse::from)
                .toList();
    }

    /** 단건 조회 */
    public WeeklyReportResponse findById(Long id) {
        return WeeklyReportResponse.from(getOrThrow(id));
    }

    /** 수정 */
    @Transactional
    public WeeklyReportResponse update(Long id, WeeklyReportRequest request) {
        WeeklyReport report = getOrThrow(id);
        // 더티 체킹: 트랜잭션 커밋 시점에 변경분이 자동 UPDATE 된다.
        report.update(
                request.getDepartment(),
                request.getName(),
                request.getPeriodStart(),
                request.getPeriodEnd(),
                request.getAccomplishments(),
                request.getNextWeekPlan(),
                request.getIssues(),
                request.getProject(),
                request.getRemarks()
        );
        // flush 를 강제해 @LastModifiedDate(updatedAt)가 응답 이전에 반영되도록 한다.
        repository.flush();
        return WeeklyReportResponse.from(report);
    }

    /** 삭제 */
    @Transactional
    public void delete(Long id) {
        WeeklyReport report = getOrThrow(id);
        repository.delete(report);
    }

    /** 공통: id 로 조회하되 없으면 404 로 이어질 예외를 던진다. */
    private WeeklyReport getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "주간보고를 찾을 수 없습니다. id=" + id));
    }
}
