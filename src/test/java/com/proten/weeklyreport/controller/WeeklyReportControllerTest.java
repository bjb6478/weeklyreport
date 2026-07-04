package com.proten.weeklyreport.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proten.weeklyreport.dto.WeeklyReportRequest;
import com.proten.weeklyreport.repository.WeeklyReportRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Controller 통합 테스트 - 실제 스프링 컨텍스트 + H2 로 HTTP 요청부터 DB 까지 관통 검증.
 * 각 테스트는 시작 전 데이터를 비워 서로 간섭하지 않게 한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class WeeklyReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WeeklyReportRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    private WeeklyReportRequest validRequest() {
        WeeklyReportRequest req = new WeeklyReportRequest();
        req.setDepartment("개발팀");
        req.setName("배정빈");
        req.setPeriodStart(LocalDate.of(2026, 6, 29));
        req.setPeriodEnd(LocalDate.of(2026, 7, 3));
        req.setAccomplishments("주간보고 API 구현");
        req.setNextWeekPlan("테스트 코드 작성");
        return req;
    }

    private String json(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    /** 테스트용 데이터 1건을 저장하고 id 반환 */
    private Long saveOne() {
        return repository.save(validRequest().toEntity()).getId();
    }

    @Test
    @DisplayName("POST - 정상 등록 시 201 과 저장된 리소스를 반환한다")
    void create_success() throws Exception {
        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.department", is("개발팀")))
                .andExpect(jsonPath("$.name", is("배정빈")))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("POST - 필수값(부서/성명) 누락 시 400 과 필드 에러를 반환한다")
    void create_validationFail() throws Exception {
        WeeklyReportRequest req = validRequest();
        req.setDepartment(null);
        req.setName("");

        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.fieldErrors.department").exists())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    @DisplayName("GET 목록 - 전체 조회")
    void findAll() throws Exception {
        saveOne();
        saveOne();

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET 목록 - 부서 필터")
    void findAll_filterByDepartment() throws Exception {
        saveOne(); // 개발팀 데이터 1건
        WeeklyReportRequest other = validRequest(); // 다른 부서 데이터 1건
        other.setDepartment("영업팀");
        repository.save(other.toEntity());

        mockMvc.perform(get("/api/reports").param("department", "개발팀"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].department", is("개발팀")));
    }

    @Test
    @DisplayName("GET 단건 - 존재하면 200")
    void findById_found() throws Exception {
        Long id = saveOne();

        mockMvc.perform(get("/api/reports/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())));
    }

    @Test
    @DisplayName("GET 단건 - 없으면 404")
    void findById_notFound() throws Exception {
        mockMvc.perform(get("/api/reports/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PUT - 수정 시 변경 내용이 반영된다")
    void update_success() throws Exception {
        Long id = saveOne();
        WeeklyReportRequest req = validRequest();
        req.setAccomplishments("수정된 실적");
        req.setRemarks("수정됨");

        mockMvc.perform(put("/api/reports/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accomplishments", is("수정된 실적")))
                .andExpect(jsonPath("$.remarks", is("수정됨")));
    }

    @Test
    @DisplayName("PUT - 없는 id 수정 시 404")
    void update_notFound() throws Exception {
        mockMvc.perform(put("/api/reports/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE - 삭제 시 204, 이후 조회하면 404")
    void delete_success() throws Exception {
        Long id = saveOne();

        mockMvc.perform(delete("/api/reports/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/reports/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE - 없는 id 삭제 시 404")
    void delete_notFound() throws Exception {
        mockMvc.perform(delete("/api/reports/{id}", 999999))
                .andExpect(status().isNotFound());
    }
}
