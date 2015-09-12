package com.logviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Dimmy Junior on 31/07/2015.
 */
@ComponentScan
@EnableAutoConfiguration
public class Application extends com.logviewer.SpringBeans {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
