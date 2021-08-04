package ru.abe.slaves.potrebot;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@RequiredArgsConstructor
public class PotrebotApplication {
    public static void main(String[] args) {
        SpringApplication.run(PotrebotApplication.class, args);
    }
}
