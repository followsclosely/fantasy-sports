package io.github.followsclosley.fantasy.nfl.playoff.generator;

import io.github.followsclosley.fantasy.nfl.playoff.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Uses recursion to calculate the total points of every possible combination of players
 * in a roster.
 */
@Component
public class BruteRecursiveRosterGenerator implements RosterGenerator {

    @Value("${fantasy.nfl.roster-generator.brute-recursive.debug:false}")
    private final boolean debug = false;

    /**
     * Given the player pool and roster limits, generate the optimal lineup using recursion.
     *
     * @param pool           Available Players
     * @param rosterSettings Roster Limits
     * @return The optimal lineup
     */
    @Override
    public List<Roster> generate(PlayerPool pool, RosterSettings rosterSettings) {
        Context context = tryAllPlayersAtThisDepth(new Context(pool, rosterSettings), new Roster(rosterSettings), 0);
        return List.of(context.bestRoster);
    }

    /**
     * Loops thought all the valid player for this spot [depth] on the roster and excursively calls
     * itself for the next level down.
     *
     * @param context Holds the players and position at each depth
     * @param roster Current roster being simulated
     * @param depth the recursion depth and also the index of the roster position being filled
     *
     * @return the Context used
     */
    public Context tryAllPlayersAtThisDepth(Context context, Roster roster, int depth) {
        if (roster.isFull()) {
            if (context.numberOfRosters++ == 0 || context.bestRoster.getPoints() < roster.getPoints()) {
                context.bestRoster = roster;
            }

            if (debug && context.numberOfRosters % 1000000 == 0) {
                System.out.print("\r[" + context.numberOfRosters + "] : " + context.bestRoster.toString(13));
            }

        } else {
            Position positionToFill = context.positionAtDepth.get(depth);
            List<Player> players = context.playerPools.get(positionToFill);
            for (Player player : players) {
                if (roster.canAddPlayer(player).isPresent()) {
                    tryAllPlayersAtThisDepth(context, roster.cloneAndAdd(player), depth + 1);
                }
            }
        }

        return context;
    }

    /**
     * Class to hold all the state needed to make the recursive call.
     *
     * <p>
     *     This inner class is used and passed in the recursive
     *     method so that the RosterGenerator class can be thread safe.
     * </p>
     */
    public static class Context {
        //Holds all the positions of the roster expanding so that ever recursive depth has a position to work with.
        List<Position> positionAtDepth;

        //List of players for a given Position
        Map<Position, List<Player>> playerPools;

        //The total number of simulated rosters
        int numberOfRosters = 0;

        //The best roster
        Roster bestRoster;

        public Context(final PlayerPool pool, RosterSettings settings) {
            this.positionAtDepth = settings.getPositionArray(true);
            this.playerPools = new HashMap<>(settings.getSize());
            for (Position p : settings.getPositions()) {
                this.playerPools.put(p, pool.getPlayers(p).limit(settings.getSize()).collect(Collectors.toList()));
            }
        }
    }
}
