package org.antwhale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: 何欢
 * @Date: 2022/10/1116:16
 * @Description:
 */
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"org.antwhale"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
