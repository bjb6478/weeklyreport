package com.proten.weeklyreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 애플리케이션 진입점.
 * main() 에서 내장 톰캣이 뜨고 스프링 컨텍스트가 초기화된다.
 */
@SpringBootApplication
public class WeeklyReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeeklyReportApplication.class, args);
    }
}
