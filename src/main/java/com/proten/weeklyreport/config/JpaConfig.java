package com.proten.weeklyreport.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정.
 * 이게 있어야 엔티티의 @CreatedDate / @LastModifiedDate 가 자동으로 채워진다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
