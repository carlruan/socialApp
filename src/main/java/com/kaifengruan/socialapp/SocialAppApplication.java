package com.kaifengruan.socialapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SocialAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialAppApplication.class, args);
    }

}
