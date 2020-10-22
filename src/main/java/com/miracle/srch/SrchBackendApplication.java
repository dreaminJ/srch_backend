package com.miracle.srch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories (basePackages = "com.miracle.srch")
public class SrchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrchBackendApplication.class, args);
    }

}
