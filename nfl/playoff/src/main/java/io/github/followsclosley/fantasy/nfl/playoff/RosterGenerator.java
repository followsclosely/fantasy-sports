package io.github.followsclosley.fantasy.nfl.playoff;

import java.util.List;

/**
 * This interface captures the act of generating an optimal lineup.
 *
 * @author M.L. Wilson
 */
public interface RosterGenerator {

    /**
     * Given the player pool and roster limits, generate the optimal lineup.
     *
     * @param pool           Available Players
     * @param rosterSettings Roster Limits
     * @return The optimal lineup
     */
    List<Roster> generate(PlayerPool pool, RosterSettings rosterSettings);
}