package io.github.followsclosley.fantasy.nfl.playoff.generator;

import io.github.followsclosley.fantasy.nfl.playoff.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This RosterGenerator takes the highest scoring player available until the
 * roster is filled.
 */
@Component
public class MaxPointsLineupGenerator implements RosterGenerator {
    public List<Roster> generate(PlayerPool pool, RosterSettings rosterSettings) {
        Roster roster = new Roster();

        Player best = getBestAvailable(pool, roster, rosterSettings);
        while (best != null) {
            roster.addPlayer(best);
            best = getBestAvailable(pool, roster, rosterSettings);
        }

        return List.of(roster.orderPlayers());
    }

    public Player getBestAvailable(PlayerPool pool, Roster roster, RosterSettings rosterSettings) {
        Player best = null;

        for (String position : rosterSettings.getPositions()) {
            List<Player> players = pool.getPlayers(position);
            for (Player player : players) {
                if (roster.canAddPlayer(player, rosterSettings)) {
                    if (best == null || best.getPoints() < player.getPoints()) {
                        best = player;
                    }
                }
            }
        }

        return best;
    }
}