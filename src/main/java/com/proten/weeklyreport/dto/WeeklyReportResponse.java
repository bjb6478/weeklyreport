package com.proten.weeklyreport.dto;

import com.proten.weeklyreport.entity.WeeklyReport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 조회 응답 DTO.
 *
 * <p>엔티티를 그대로 반환하지 않고 이 객체로 변환해서 내보낸다.
 * 노출할 필드를 API 가 통제할 수 있고, 엔티티 구조 변경이 응답 스펙으로 새어나가지 않는다.</p>
 */
@Getter
@Builder
public class WeeklyReportResponse {

    private Long id;
    private String department;
    private String name;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String accomplishments;
    private String nextWeekPlan;
    private String issues;
    private String project;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 엔티티 -> 응답 DTO 변환 */
    public static WeeklyReportResponse from(WeeklyReport entity) {
        return WeeklyReportResponse.builder()
                .id(entity.getId())
                .department(entity.getDepartment())
                .name(entity.getName())
                .periodStart(entity.getPeriodStart())
                .periodEnd(entity.getPeriodEnd())
                .accomplishments(entity.getAccomplishments())
                .nextWeekPlan(entity.getNextWeekPlan())
                .issues(entity.getIssues())
                .project(entity.getProject())
                .remarks(entity.getRemarks())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
