package com.train.member.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.train")
public class MemberApplication {
    public static final Logger logger = LoggerFactory.getLogger(MemberApplication.class);
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MemberApplication.class);
        Environment env = application.run(args).getEnvironment();
        logger.info("=== 启动成功 ===");
        logger.info("地址: \thttp://127.0.0.1:{}/member/hello", env.getProperty("server.port"));
    }
}
