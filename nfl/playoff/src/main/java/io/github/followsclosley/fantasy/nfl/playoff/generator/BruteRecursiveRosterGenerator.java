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

    @Override
    public List<Roster> generate(PlayerPool pool, RosterSettings rosterSettings) {
        Context context = recursive(new Context(pool, rosterSettings), new Roster(rosterSettings), 0);
        return List.of(context.bestRoster);
    }

    public Context recursive(Context context, Roster roster, int depth) {
        if (roster.isFull()) {
            //System.out.println("Roster Complete " + roster);
            context.numberOfRosters++;
            if (context.bestRoster == null || context.bestRoster.getPoints() < roster.getPoints()) {
                context.bestRoster = roster;
                if( debug ) {
                    System.out.print("\r[" + context.numberOfRosters + "] : " + context.bestRoster.toString(13));
                }
            }

            if( debug && context.numberOfRosters % 1000000 == 0){
                System.out.print("\r[" + context.numberOfRosters + "] : " + context.bestRoster.toString(13));
            }
        } else {
            Position positionToFill = context.positionAtDepth.get(depth);
            List<Player> players = context.playerPools.get(positionToFill);
            for (Player player : players) {
                if (roster.canAddPlayer(player).isPresent()) {
                    recursive(context, roster.addPlayerAndClone(player), depth + 1);
                }
            }
        }

        return context;
    }

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
