package com.learnnow;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BackendApplication {
    Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
