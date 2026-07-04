package com.proten.weeklyreport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

/**
 * 주간보고 엔티티 - DB 테이블(weekly_report)과 1:1로 매핑되는 도메인 객체.
 *
 * <p>영속성 관심사(테이블/컬럼 매핑)만 담당하고, 외부(HTTP)로 그대로 노출하지 않는다.
 * 노출은 Response DTO 를 통해서 한다.</p>
 */
@Entity
@Table(name = "weekly_report")
@EntityListeners(AuditingEntityListener.class) // createdAt/updatedAt 자동 세팅
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 는 기본 생성자 필요, 외부 직접 생성은 막음
public class WeeklyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    private Long id;

    /** 부서 */
    @Column(nullable = false)
    private String department;

    /** 성명 */
    @Column(nullable = false)
    private String name;

    /** 보고 시작일 */
    @Column(nullable = false)
    private LocalDate periodStart;

    /** 보고 종료일 */
    @Column(nullable = false)
    private LocalDate periodEnd;

    /** 금주 실적 */
    @Column(columnDefinition = "TEXT")
    private String accomplishments;

    /** 차주 계획 */
    @Column(columnDefinition = "TEXT")
    private String nextWeekPlan;

    /** 이슈사항 */
    @Column(columnDefinition = "TEXT")
    private String issues;

    /** 프로젝트 */
    @Column(columnDefinition = "TEXT")
    private String project;

    /** 비고 */
    @Column(columnDefinition = "TEXT")
    private String remarks;

    /** 등록 시각 (최초 저장 시 자동 세팅, 이후 불변) */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 수정 시각 (저장/수정마다 자동 갱신) */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    private WeeklyReport(String department, String name, LocalDate periodStart, LocalDate periodEnd,
                         String accomplishments, String nextWeekPlan, String issues,
                         String project, String remarks) {
        this.department = department;
        this.name = name;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.accomplishments = accomplishments;
        this.nextWeekPlan = nextWeekPlan;
        this.issues = issues;
        this.project = project;
        this.remarks = remarks;
    }

    /**
     * 수정용 메서드 - 엔티티 스스로 상태 변경을 책임진다(setter 남발 대신 의도가 드러나는 메서드).
     */
    public void update(String department, String name, LocalDate periodStart, LocalDate periodEnd,
                       String accomplishments, String nextWeekPlan, String issues,
                       String project, String remarks) {
        this.department = department;
        this.name = name;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.accomplishments = accomplishments;
        this.nextWeekPlan = nextWeekPlan;
        this.issues = issues;
        this.project = project;
        this.remarks = remarks;
    }
}
