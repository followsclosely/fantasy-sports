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
     * Executes all the LineupGenerator instances that exists in the spring context.
     *
     * @param lineupGenerators All the LineupGenerator instances available
     * @param playerPool       Available Players
     * @param rosterSettings   Roster Limits
     * @return the code to run the LineupGenerator instances
     */
    @Bean
    public CommandLineRunner getCommandLineRunner(List<LineupGenerator> lineupGenerators, PlayerPool playerPool, RosterSettings rosterSettings) {
        return args -> {
            for (LineupGenerator lineupGenerator : lineupGenerators) {
                long start = System.currentTimeMillis();
                List<Roster> sortedRosters = lineupGenerator.generate(playerPool, rosterSettings);
                System.out.println("(" + (System.currentTimeMillis() - start) + "ms) " + lineupGenerator.getClass().getName());
                for (int i = 0, size = Math.min(sortedRosters.size(), 20); (i < size); i++) {
                    System.out.println(sortedRosters.get(i));
                }
                System.out.println();

            }
        };
    }
}