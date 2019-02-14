package com.fr.swift.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class created on 2018/10/30
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SpringBootApplication
public class SwiftSpringBootApplication {
    public static void main(String[] args) {
        SwiftEngineStart.start(args);
        SpringApplication.run(SwiftSpringBootApplication.class, args);
    }
}
