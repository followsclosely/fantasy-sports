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

    @Override
    public List<Roster> generate(PlayerPool pool, RosterSettings rosterSettings) {
        Context context = new Context(pool, rosterSettings);
        recursive(context, new Roster(rosterSettings), 0);
        return List.of(context.bestRoster);
    }

    public void recursive(Context context, Roster roster, int depth) {
        if (roster.isFull()) {
            //System.out.println("Roster Complete " + roster);
            context.addRoster(roster);
        } else {
            Position positionToFill = context.positionAtDepth.get(depth);
            List<Player> players = context.playerPools.get(positionToFill);
            for (Player player : players) {
                if (roster.canAddPlayer(player).isPresent()) {
                    recursive(context, roster.addPlayerAndClone(player), depth + 1);
                }
            }
        }
    }

    public static class Context {

        @Value("${fantasy.nfl.roster-generator.brute-recursive.debug:false}")
        private final boolean debug = false;

        //Holds all the positions of the roster expanding so that ever recursive depth has a position to work with.
        List<Position> positionAtDepth;

        //List of players for a given Position
        Map<Position, List<Player>> playerPools;

        int numberOfRosters = 0;
        Roster bestRoster;

        public Context(final PlayerPool pool, RosterSettings settings) {
            this.positionAtDepth = settings.getPositionArray(true);
            this.playerPools = new HashMap<>(settings.getSize());
            for (Position p : settings.getPositions()) {
                this.playerPools.put(p, pool.getPlayers(p).limit(settings.getSize()).collect(Collectors.toList()));
            }
        }

        public void addRoster(Roster roster) {
            numberOfRosters++;
            if (bestRoster == null || bestRoster.getPoints() < roster.getPoints()) {
                bestRoster = roster;
                if( debug ) {
                    System.out.print("\r[" + numberOfRosters + "] : " + bestRoster.toString(13));
                }
            }

            if( debug && numberOfRosters % 1000000 == 0){
                System.out.print("\r[" + numberOfRosters + "] : " + bestRoster.toString(13));
            }
        }
    }
}
