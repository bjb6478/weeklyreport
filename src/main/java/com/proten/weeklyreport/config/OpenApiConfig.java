package com.proten.weeklyreport.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 문서 메타정보 설정.
 *
 * <p>애플리케이션 실행 후 접속:
 *   - Swagger UI : http://localhost:8080/swagger-ui.html
 *   - OpenAPI JSON: http://localhost:8080/v3/api-docs</p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI weeklyReportOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("주간보고(Weekly Report) 관리 API")
                        .description("부서별 주간 업무 보고를 등록·조회·수정·삭제하는 REST API")
                        .version("v0.0.1")
                        .contact(new Contact()
                                .name("weeklyreport")
                                .email("jbbae@proten.co.kr")));
    }
}
