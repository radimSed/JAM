package com.jam;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

@Configuration
@ComponentScan
public class JAMApplicationConfiguration {
    @Bean
    public EntityManagerFactory entityManagerFactory(){
            return createEntityManagerFactory("com.JAM");
    }
}
