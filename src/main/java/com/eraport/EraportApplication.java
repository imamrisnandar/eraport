
// EraportApplication.java
package com.eraport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class EraportApplication {

    public static void main(String[] args) {
        SpringApplication.run(EraportApplication.class, args);
    }
}
