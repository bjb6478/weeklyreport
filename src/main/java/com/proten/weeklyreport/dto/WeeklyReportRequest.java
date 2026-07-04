package com.proten.weeklyreport.dto;

import com.proten.weeklyreport.entity.WeeklyReport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 등록/수정 요청 DTO.
 *
 * <p>클라이언트가 보낸 JSON 이 바인딩되는 곳이자 검증(@Valid)의 대상.
 * 엔티티를 직접 요청 바디로 받지 않는 이유:
 *   - id/createdAt 같은 서버 관리 필드가 외부에서 주입되는 것을 막고,
 *   - 검증 규칙을 도메인과 분리해 API 스펙 변화에 유연하게 대응하기 위함.</p>
 */
@Getter
@Setter
@NoArgsConstructor
public class WeeklyReportRequest {

    @NotBlank(message = "부서는 필수입니다.")
    private String department;

    @NotBlank(message = "성명은 필수입니다.")
    private String name;

    @NotNull(message = "보고 시작일은 필수입니다.")
    private LocalDate periodStart;

    @NotNull(message = "보고 종료일은 필수입니다.")
    private LocalDate periodEnd;

    // 아래 긴 텍스트 항목들은 선택 입력
    private String accomplishments;
    private String nextWeekPlan;
    private String issues;
    private String project;
    private String remarks;

    /** 요청 DTO -> 새 엔티티로 변환 (등록 시 사용) */
    public WeeklyReport toEntity() {
        return WeeklyReport.builder()
                .department(department)
                .name(name)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .accomplishments(accomplishments)
                .nextWeekPlan(nextWeekPlan)
                .issues(issues)
                .project(project)
                .remarks(remarks)
                .build();
    }
}
