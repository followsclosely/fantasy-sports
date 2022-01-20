package io.github.followsclosley.fantasy.nfl.playoff.generator;

import io.github.followsclosley.fantasy.nfl.playoff.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This RosterGenerator leverages Value-based drafting (VBD) to draft the optimal lineup.
 * <p>
 * Value-based drafting (VBD) is a strategy that assigns a value to every player by comparing that player's
 * fantasy points to the fantasy points of a baseline player at the same position. A high VBD means a player
 * offers strong incremental value at his position and is therefore valuable overall in fantasy.
 * - https://www.fantasypros.com/
 * </p>
 */
@Component
public class ValueLineupGenerator implements RosterGenerator {

    @Value("${fantasy.nfl.roster-generator.value.debug:false}")
    private final boolean debug = false;

    /**
     * Uses Value-based drafting (VBD) to draft the optimal lineup.
     *
     * @param pool           Available Players
     * @param rosterSettings Roster Limits
     * @return Almost optimal roster
     */
    public List<Roster> generate(PlayerPool pool, RosterSettings rosterSettings) {
        Roster roster = new Roster();

        while (true) {
            List<Delta> deltas = getDeltas(pool, rosterSettings, roster);

            if (deltas.size() > 0) {
                roster.addPlayer(deltas.get(0).getPlayer());
                if (debug) debug(roster, deltas);
            } else {
                break;
            }
        }

        return List.of(roster.orderPlayers());
    }


    /**
     * Returns a List of Delta objects. Each Delta object holds the best
     * <b>starter</b> and the best <b>non starter</b> for a given position. For
     * example if your rosterSettings say you can have two RBs on your roster
     * then the Delta will have RB1 and RB3.
     *
     * @return The list of Delta(s) is sorted by the difference in fantasy
     * points between the players in the Delta.
     */
    private List<Delta> getDeltas(PlayerPool pool, RosterSettings rosterSettings, Roster roster) {
        List<Delta> deltas = new ArrayList<>();

        for (String position : rosterSettings.getPositions()) {
            Integer depth = rosterSettings.getLimit(position);
            List<Player> players = pool.getPlayers(position).collect(Collectors.toList());

            Delta delta = null;
            for (Player nextPlayer : players) {
                if (roster.getPlayers().contains(nextPlayer)) {
                    --depth;
                } else if (delta == null && roster.canAddPlayer(nextPlayer, rosterSettings)) {
                    delta = new Delta(nextPlayer);
                } else {
                    if (roster.canAddPlayer(nextPlayer, rosterSettings) && (--depth == 0)) {
                        delta.setNextBest(nextPlayer);
                    }
                }
            }

            if (delta != null) {
                deltas.add(delta);
            }
        }

        deltas.sort(Comparator.comparingDouble(Delta::getValue).reversed());
        return deltas;
    }

    private void debug(Roster roster, List<Delta> deltas) {
        for (Delta delta : deltas) {
            System.out.println(delta.getPlayer().getPosition() + "\t" + String.format("%.2f", delta.getValue())
                    + " =\t " + delta.getPlayer() + "(" + delta.getPlayer().getPoints() + ")\t-  " + delta.getNextBest()
                    + "(" + (delta.getNextBest() == null ? "0" : delta.getNextBest().getPoints()) + ")");
        }
        System.out.println(roster + "\n");
    }

    public static class Delta {
        private final Player player;
        private float value;
        private Player nextBest;

        public Delta(Player player) {
            this.player = player;
        }

        public float getValue() {
            return value;
        }

        public Player getPlayer() {
            return player;
        }

        public Player getNextBest() {
            return nextBest;
        }

        public void setNextBest(Player nextBest) {
            this.nextBest = nextBest;
            this.value = player.getPoints() - nextBest.getPoints();
        }
    }
}