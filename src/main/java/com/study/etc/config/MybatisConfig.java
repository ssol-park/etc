package com.study.etc.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.study.etc.reword")
public class MybatisConfig {
}
