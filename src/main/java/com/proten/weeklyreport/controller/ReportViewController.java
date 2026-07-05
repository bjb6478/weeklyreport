package com.proten.weeklyreport.controller;

import com.proten.weeklyreport.dto.WeeklyReportRequest;
import com.proten.weeklyreport.dto.WeeklyReportResponse;
import com.proten.weeklyreport.service.WeeklyReportService;
import jakarta.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 화면(View) 전용 컨트롤러.
 *
 * <p>REST API 컨트롤러({@link WeeklyReportController})와 역할을 분리한다.
 *   - REST 컨트롤러 : JSON 을 주고받는 @RestController (기계용 API)
 *   - 이 컨트롤러   : Thymeleaf 템플릿 이름을 반환하는 @Controller (사람용 화면)
 * 둘 다 같은 {@link WeeklyReportService} 를 재사용하므로 비즈니스 로직 중복이 없다.</p>
 */
@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportViewController {

    private final WeeklyReportService service;

    /** 목록 화면 */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("reports", service.findAll(null, null, null, null));
        return "reports/list";
    }

    /** 등록 폼 화면 (보고 기간은 오늘이 속한 주의 월~일로 기본 세팅) */
    @GetMapping("/new")
    public String newForm(Model model) {
        WeeklyReportRequest form = new WeeklyReportRequest();
        LocalDate today = LocalDate.now();
        form.setPeriodStart(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        form.setPeriodEnd(today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
        model.addAttribute("form", form);
        prepareForm(model, "새 주간보고 등록", "/reports");
        return "reports/form";
    }

    /** 등록 처리 (검증 실패 시 폼으로 되돌아가 에러 표시) */
    @PostMapping
    public String create(@Valid @ModelAttribute("form") WeeklyReportRequest form,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, "새 주간보고 등록", "/reports");
            return "reports/form";
        }
        service.create(form);
        return "redirect:/reports";
    }

    /** 수정 폼 화면 (기존 값 채워서 보여줌) */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        WeeklyReportResponse report = service.findById(id); // 없으면 404 (GlobalExceptionHandler)
        model.addAttribute("form", toRequest(report));
        prepareForm(model, "주간보고 수정", "/reports/" + id);
        return "reports/form";
    }

    /** 수정 처리 (검증 실패 시 폼으로 되돌아가 에러 표시) */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") WeeklyReportRequest form,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, "주간보고 수정", "/reports/" + id);
            return "reports/form";
        }
        service.update(id, form);
        return "redirect:/reports";
    }

    /** 삭제 처리 후 목록으로 (삭제 확인은 화면의 JS confirm 에서 처리) */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/reports";
    }

    /** 폼 화면 공통 모델 세팅 (제목, 제출 URL) */
    private void prepareForm(Model model, String pageTitle, String formAction) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("formAction", formAction);
    }

    /** 응답 DTO 값을 수정 폼용 요청 DTO 로 옮긴다. */
    private WeeklyReportRequest toRequest(WeeklyReportResponse res) {
        WeeklyReportRequest req = new WeeklyReportRequest();
        req.setDepartment(res.getDepartment());
        req.setName(res.getName());
        req.setPeriodStart(res.getPeriodStart());
        req.setPeriodEnd(res.getPeriodEnd());
        req.setAccomplishments(res.getAccomplishments());
        req.setNextWeekPlan(res.getNextWeekPlan());
        req.setIssues(res.getIssues());
        req.setProject(res.getProject());
        req.setRemarks(res.getRemarks());
        return req;
    }
}
