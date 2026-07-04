package com.proten.weeklyreport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.proten.weeklyreport.dto.WeeklyReportRequest;
import com.proten.weeklyreport.dto.WeeklyReportResponse;
import com.proten.weeklyreport.entity.WeeklyReport;
import com.proten.weeklyreport.exception.ResourceNotFoundException;
import com.proten.weeklyreport.repository.WeeklyReportRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Service 단위 테스트 - Repository 를 모킹해서 비즈니스 로직만 검증한다.
 * DB 없이 빠르게 돌고, "없는 id -> 예외" 같은 분기를 정확히 확인할 수 있다.
 */
@ExtendWith(MockitoExtension.class)
class WeeklyReportServiceTest {

    @Mock
    private WeeklyReportRepository repository;

    @InjectMocks
    private WeeklyReportService service;

    private WeeklyReportRequest sampleRequest() {
        WeeklyReportRequest req = new WeeklyReportRequest();
        req.setDepartment("개발팀");
        req.setName("배정빈");
        req.setPeriodStart(LocalDate.of(2026, 6, 29));
        req.setPeriodEnd(LocalDate.of(2026, 7, 3));
        req.setAccomplishments("주간보고 API 구현");
        return req;
    }

    /** id 가 세팅된 엔티티(영속 상태 흉내) */
    private WeeklyReport persistedEntity(long id) {
        WeeklyReport entity = sampleRequest().toEntity();
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    @Test
    @DisplayName("등록 - 저장 후 응답 DTO 를 반환한다")
    void create_returnsResponse() {
        when(repository.save(any(WeeklyReport.class))).thenReturn(persistedEntity(1L));

        WeeklyReportResponse result = service.create(sampleRequest());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDepartment()).isEqualTo("개발팀");
        verify(repository, times(1)).save(any(WeeklyReport.class));
    }

    @Test
    @DisplayName("단건 조회 - 존재하면 응답 DTO 를 반환한다")
    void findById_found() {
        when(repository.findById(1L)).thenReturn(Optional.of(persistedEntity(1L)));

        WeeklyReportResponse result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("배정빈");
    }

    @Test
    @DisplayName("단건 조회 - 없으면 ResourceNotFoundException")
    void findById_notFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("수정 - 존재하면 필드가 갱신되고 flush 가 호출된다")
    void update_found() {
        WeeklyReport entity = persistedEntity(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        WeeklyReportRequest req = sampleRequest();
        req.setAccomplishments("수정된 실적");
        WeeklyReportResponse result = service.update(1L, req);

        assertThat(result.getAccomplishments()).isEqualTo("수정된 실적");
        verify(repository, times(1)).flush();
    }

    @Test
    @DisplayName("수정 - 없으면 예외를 던지고 flush 하지 않는다")
    void update_notFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999L, sampleRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).flush();
    }

    @Test
    @DisplayName("삭제 - 존재하면 delete 를 호출한다")
    void delete_found() {
        WeeklyReport entity = persistedEntity(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("삭제 - 없으면 예외를 던지고 delete 하지 않는다")
    void delete_notFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).delete(any(WeeklyReport.class));
    }

    @Test
    @DisplayName("목록 조회 - Specification 과 정렬로 조회해 응답 리스트를 반환한다")
    void findAll_returnsList() {
        when(repository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(persistedEntity(1L), persistedEntity(2L)));

        List<WeeklyReportResponse> result =
                service.findAll("개발팀", null, null, null);

        assertThat(result).hasSize(2);
        verify(repository).findAll(any(Specification.class), any(Sort.class));
    }
}
