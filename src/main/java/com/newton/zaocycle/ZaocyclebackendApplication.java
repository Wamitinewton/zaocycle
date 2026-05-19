package com.newton.zaocycle;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ZaocyclebackendApplication {

    public static void main(String[] args) {
        Dotenv.configure()
                .ignoreIfMissing()
                .load()
                .entries()
                .forEach(e -> {
                    if (System.getenv(e.getKey()) == null) {
                        System.setProperty(e.getKey(), e.getValue());
                    }
                });
        SpringApplication.run(ZaocyclebackendApplication.class, args);
    }
}
