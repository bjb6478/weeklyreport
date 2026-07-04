package com.proten.weeklyreport.repository;

import com.proten.weeklyreport.entity.WeeklyReport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * 동적 검색 조건 조립기.
 *
 * <p>넘어온 파라미터 중 값이 있는 것만 AND 조건으로 붙인다.
 * 예) 부서만 주면 부서로만, 부서+기간을 주면 둘 다로 필터링.</p>
 *
 * 기간 필터 규칙: [from, to] 와 보고 기간이 겹치는 보고서를 찾는다.
 *   - from 이 주어지면: periodEnd >= from   (보고 종료일이 검색 시작일 이후)
 *   - to 가 주어지면  : periodStart <= to   (보고 시작일이 검색 종료일 이전)
 */
public final class WeeklyReportSpecification {

    private WeeklyReportSpecification() {
    }

    public static Specification<WeeklyReport> filter(String department, String name,
                                                     LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(department)) {
                predicates.add(cb.equal(root.get("department"), department));
            }
            if (StringUtils.hasText(name)) {
                predicates.add(cb.equal(root.get("name"), name));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("periodEnd"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("periodStart"), to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
