# weeklyreport

주간보고(Weekly Report)를 등록·조회·수정·삭제하는 Spring Boot 웹 애플리케이션입니다.
REST API와 Thymeleaf 기반 웹 화면을 함께 제공합니다.

## 기술 스택

- Java 17 (컴파일 타겟) / Gradle 9.6.1 (Wrapper)
- Spring Boot 3.5.4 — Web, Data JPA, Validation, Thymeleaf
- H2 Database (개발용 인메모리)
- springdoc-openapi 2.8.6 (Swagger UI)
- Lombok

## 주요 기능

- 주간보고 CRUD (등록/목록/단건/수정/삭제)
- 부서·성명·기간으로 목록 필터링
- 필수값(부서/성명/기간) 검증 및 에러 메시지
- 없는 데이터 조회 시 404 응답
- Thymeleaf 웹 화면 (목록/등록/수정 폼)
- Swagger API 문서 자동 생성
- 단위·통합 테스트 18개

## 프로젝트 구조

```
src/main/java/com/proten/weeklyreport/
├─ WeeklyReportApplication.java   # 앱 진입점
├─ config/                        # JPA Auditing, OpenAPI 설정
├─ controller/
│   ├─ WeeklyReportController.java # REST API (@RestController)
│   └─ ReportViewController.java   # 웹 화면 (@Controller)
├─ dto/                           # 요청/응답 DTO
├─ entity/                        # WeeklyReport 엔티티
├─ exception/                     # 404·검증 예외 처리
├─ repository/                    # JPA Repository + 동적 필터
└─ service/                       # 비즈니스 로직
```

## 실행 방법

```bash
./gradlew bootRun
```

실행 후 브라우저에서 접속:

| 화면 | 주소 |
|------|------|
| 웹 화면 (목록) | http://localhost:8080/reports |
| Swagger 문서 | http://localhost:8080/swagger-ui.html |
| H2 콘솔 | http://localhost:8080/h2-console |

## REST API

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/reports` | 등록 |
| GET | `/api/reports` | 목록 조회 (부서/성명/기간 필터) |
| GET | `/api/reports/{id}` | 단건 조회 |
| PUT | `/api/reports/{id}` | 수정 |
| DELETE | `/api/reports/{id}` | 삭제 |

## 테스트

```bash
./gradlew test
```
