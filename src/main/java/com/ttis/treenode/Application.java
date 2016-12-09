package com.ttis.treenode;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@SpringBootApplication
@EnableAutoConfiguration(exclude = { })
@ComponentScan(basePackages = {"com.ttis.treenode.controller","com.ttis.treenode.service"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    public static class SimpleConfiguration{

        @Value("${treenode.show-json-pretty:false}")
        boolean showJSONPretty;

        @Bean
        @Qualifier("showJSONPretty")
        boolean showJSONPretty(){
            return showJSONPretty;
        }

        @Bean
        public ThreadPoolTaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(5);
            executor.setMaxPoolSize(10);
            executor.setQueueCapacity(25);
            return executor;
        }
    }


}
