package com.proten.weeklyreport.repository;

import com.proten.weeklyreport.entity.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 영속성 계층. DB 접근(CRUD 쿼리)을 담당한다.
 *
 * <p>JpaRepository        : 기본 CRUD(save/findById/findAll/delete ...) 자동 제공.
 * JpaSpecificationExecutor: 조건이 동적으로 바뀌는 검색(findAll(Specification))을 지원.
 *   → 부서/성명/기간 필터가 있을 수도, 없을 수도 있으므로 Specification 으로 조립한다.</p>
 */
public interface WeeklyReportRepository
        extends JpaRepository<WeeklyReport, Long>, JpaSpecificationExecutor<WeeklyReport> {
}
