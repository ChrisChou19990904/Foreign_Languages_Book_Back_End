package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 這是 Spring Boot 應用程式的啟動類。
 *
 * @SpringBootApplication 包含了以下三個核心註解：
 * 1. @EnableAutoConfiguration: 根據 classpath 依賴自動配置 Spring 應用程式。
 * 2. @ComponentScan: 預設掃描 org.example package 及其所有子 package
 * (包括 entity, controller, service, repository, security 等) 下的所有 Spring 組件 (例如 @Component, @Service, @Controller)。
 * 3. @Configuration: 標記此類為配置類。
 */
@SpringBootApplication
public class ForeignLanguagesBookApplication {

    public static void main(String[] args) {
        // 啟動 Spring Boot 應用程式
        SpringApplication.run(ForeignLanguagesBookApplication.class, args);
        System.out.println("\n---------------------------------------------------------");
        System.out.println("🚀 Foreign Languages Book Backend 應用程式已成功啟動！");
        System.out.println("---------------------------------------------------------");
    }
}
