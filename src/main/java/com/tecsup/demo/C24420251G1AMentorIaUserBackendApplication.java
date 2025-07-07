package com.tecsup.demo;

import com.tecsup.demo.config.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class C24420251G1AMentorIaUserBackendApplication {

    public static void main(String[] args) {
        DotenvLoader.loadEnv();
        SpringApplication.run(C24420251G1AMentorIaUserBackendApplication.class, args);
    }

}
