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

    /**
     * Drafts the highest scoring player available until the roster is filled.
     *
     * @param pool           Available Players
     * @param rosterSettings Roster Limits
     * @return Almost optimal roster
     */
    public List<Roster> generate(PlayerPool pool, RosterSettings rosterSettings) {
        Roster roster = new Roster();

        Player best = getBestAvailable(pool, roster, rosterSettings);
        while (best != null) {
            roster.addPlayer(best);
            best = getBestAvailable(pool, roster, rosterSettings);
        }

        return List.of(roster.orderPlayers());
    }

    /**
     * Returns the player with the highest fantasy points for a position that is still open
     *
     * @param pool           Available Players
     * @param roster         The current roster being built
     * @param rosterSettings The roster settings
     * @return The player with the highest fantasy points
     */
    public Player getBestAvailable(PlayerPool pool, Roster roster, RosterSettings rosterSettings) {
        Player best = null;

        for (String position : rosterSettings.getPositions()) {
            for (Player player : pool.getPlayers(position)) {
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