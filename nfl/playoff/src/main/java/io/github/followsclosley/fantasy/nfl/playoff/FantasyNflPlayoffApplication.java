package io.github.followsclosley.fantasy.nfl.playoff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@SpringBootApplication
public class FantasyNflPlayoffApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FantasyNflPlayoffApplication.class, args);
        Launcher launcher = context.getBean(Launcher.class, "Launcher");
        launcher.execute();
    }
}