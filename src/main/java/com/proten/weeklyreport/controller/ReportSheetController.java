package com.proten.weeklyreport.controller;

import com.proten.weeklyreport.service.WeeklyReportService;
import com.proten.weeklyreport.view.PivotSheet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 스프레드시트형 보기 화면 전용 컨트롤러.
 *
 * <p>기존 목록/폼 화면({@link ReportViewController})과 분리했다.
 * 데이터·DB·등록/수정 로직은 그대로 두고, 같은 {@link WeeklyReportService} 를
 * 재사용해 "보기 전용" 화면만 추가한다. 보고서를 부서→성명→항목×주차 로 pivot 한다.</p>
 */
@Controller
@RequestMapping("/reports/sheet")
@RequiredArgsConstructor
public class ReportSheetController {

    private final WeeklyReportService service;

    /** 스프레드시트 보기 화면 (주차를 가로 열로 펼친 pivot 격자) */
    @GetMapping
    public String sheet(Model model) {
        PivotSheet sheet = PivotSheet.of(service.findAll(null, null, null, null));
        model.addAttribute("sheet", sheet);
        return "reports/sheet";
    }
}
