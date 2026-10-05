package com.construction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConstructionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConstructionApplication.class, args);
        System.out.println("====================================================================");
        System.out.println("🚀 Construction Management System Backend Started Successfully!");
        System.out.println("🌐 Access Web Application at: http://localhost:8080");
        System.out.println("====================================================================");
    }
}
