package io.github.followsclosley.fantasy.nfl.playoff;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan
@SpringBootApplication
public class FantasyNflPlayoffApplication {

    public static void main(String[] args) {
        SpringApplication.run(FantasyNflPlayoffApplication.class, args);
    }

    /**
     * Executes all the RosterGenerator instances that exists in the spring context.
     *
     * @param rosterGenerators All the RosterGenerator instances available
     * @param playerPool       Available Players
     * @param rosterSettings   Roster Limits
     * @return the code to run the RosterGenerator instances
     */
    @Bean
    public CommandLineRunner getCommandLineRunner(List<RosterGenerator> rosterGenerators, PlayerPool playerPool, RosterSettings rosterSettings) {
        return args -> {
            for (RosterGenerator rosterGenerator : rosterGenerators) {
                long start = System.currentTimeMillis();
                List<Roster> sortedRosters = rosterGenerator.generate(playerPool, rosterSettings);
                System.out.println("(" + (System.currentTimeMillis() - start) + "ms) " + rosterGenerator.getClass().getName());
                for (int i = 0, size = Math.min(sortedRosters.size(), 20); (i < size); i++) {
                    System.out.println(sortedRosters.get(i));
                }
                System.out.println();

            }
        };
    }
}