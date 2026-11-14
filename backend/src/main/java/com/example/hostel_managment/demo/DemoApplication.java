package com.example.hostel_managment.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.time.Clock;
import java.time.ZoneId;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {
    @Bean
    Clock applicationClock() {
        return Clock.system(ZoneId.of("Asia/Kolkata"));
    }

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
