package io.github.followsclosley.fantasy.nfl.playoff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Launcher implements CommandLineRunner {
	@Autowired
	protected PlayerPool playerPool;

	@Autowired
	protected RosterSettings rosterSettings;

	@Autowired
	protected List<LineupGenerator> lineupGenerators;

	@Override
	public void run(String... args) throws Exception {
		for (LineupGenerator lineupGenerator : lineupGenerators) {
			long start = System.currentTimeMillis();
			List<Roster> sortedRosters = lineupGenerator.generate(playerPool, rosterSettings);
			System.out.println("(" + (System.currentTimeMillis() - start) + "ms) " + lineupGenerator.getClass().getName());
			for (int i = 0, size = Math.min(sortedRosters.size(), 20); (i < size); i++) {
				System.out.println(sortedRosters.get(i));
			}
			System.out.println();

		}
	}
}
