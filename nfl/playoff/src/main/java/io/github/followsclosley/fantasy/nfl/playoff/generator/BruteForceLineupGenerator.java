package io.github.followsclosley.fantasy.nfl.playoff.generator;

import io.github.followsclosley.fantasy.nfl.playoff.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This RosterGenerator uses brute force and generates all possible lineups for the
 * following RosterSettings:
 * <ul>
 *   <li>QB: 1</li>
 *   <li>RB: 2</li>
 *   <li>WR: 2</li>
 *   <li>TE: 1</li>
 *   <li>RB|WR|TE: 1</li>
 *   <li>K: 1</li>
 *   <li>D: 1</li>
 * </ul>
 * <p>
 * Note that this implementation does not use the RosterSettings passed in. It is hard coded
 * to the above 9 positions.
 */
@ConditionalOnProperty(prefix = "command.line.runner", value = "enabled", havingValue = "true", matchIfMissing = true)
@Component
public class BruteForceLineupGenerator implements RosterGenerator {

    @Value("${fantasy.nfl.roster-generator.brute-force.number-to-return:1}")
    private final int numberToReturn = 1;

    @Value("${fantasy.nfl.roster-generator.brute-force.debug:false}")
    private final boolean debug = false;

    /**
     * Generates the optimal Roster.
     *
     * @param pool           Available Players
     * @param rosterSettings Roster Limits
     * @return The absolute optimal roster
     */
    public ArrayList<Roster> generate(PlayerPool pool, RosterSettings rosterSettings) {
        int total = 0;

        //Just the top roster.size at any given position should be enough.
        int limit = rosterSettings.getSize();

        List<Player> qbs = pool.getPlayers("QB").limit(limit).collect(Collectors.toList());
        List<Player> rbs = pool.getPlayers("RB").limit(limit).collect(Collectors.toList());
        List<Player> wrs = pool.getPlayers("WR").limit(limit).collect(Collectors.toList());
        List<Player> tes = pool.getPlayers("TE").limit(limit).collect(Collectors.toList());
        List<Player> fxs = pool.getPlayers("RB", "WR", "TE").limit(limit).collect(Collectors.toList());
        List<Player> ks = pool.getPlayers("K").limit(limit).collect(Collectors.toList());
        List<Player> ds = pool.getPlayers("D").limit(limit).collect(Collectors.toList());


        ArrayList<Roster> sortedRosters = new ArrayList<>();
        Set<String> unique = new HashSet<>();

        for (Player qb : qbs) {
            Roster roster = new Roster(rosterSettings);
            roster.addPlayer(qb);
            for (Player rb1 : rbs) {
                if (roster.canAddPlayer(rb1).isPresent()) {
                    Roster rosterRb1 = roster.addPlayerAndClone(rb1);
                    for (Player rb2 : rbs) {
                        if (rosterRb1.canAddPlayer(rb2).isPresent()) {
                            Roster rosterRb2 = rosterRb1.addPlayerAndClone(rb2);
                            for (Player wr1 : wrs) {
                                if (rosterRb2.canAddPlayer(wr1).isPresent()) {
                                    Roster rosterWr1 = rosterRb2.addPlayerAndClone(wr1);
                                    for (Player wr2 : wrs) {
                                        if (rosterWr1.canAddPlayer(wr2).isPresent()) {
                                            Roster rosterWr2 = rosterWr1.addPlayerAndClone(wr2);
                                            for (Player te : tes) {
                                                if (rosterWr2.canAddPlayer(te).isPresent()) {
                                                    Roster rosterTe = rosterWr2.addPlayerAndClone(te);
                                                    for (Player pk : ks) {
                                                        if (rosterTe.canAddPlayer(pk).isPresent()) {
                                                            Roster rosterPk = rosterTe.addPlayerAndClone(pk);
                                                            for (Player dt : ds) {
                                                                if (rosterPk.canAddPlayer(dt).isPresent()) {
                                                                    Roster rosterDt = rosterPk.addPlayerAndClone(dt);
                                                                    for (Player fx : fxs) {
                                                                        if (rosterDt.canAddPlayer(fx).isPresent()) {
                                                                            Roster rosterFx = rosterDt.addPlayerAndClone(fx);
                                                                            if (unique.add(rosterFx.getUniqueKey())) {
                                                                                sortedRosters.add(rosterFx);
                                                                            }

                                                                            if (++total % 10000 == 0) {
                                                                                sortedRosters.sort(Comparator.comparing(Roster::getPoints).reversed());
                                                                                sortedRosters.subList(numberToReturn, sortedRosters.size()).clear();
                                                                                if (debug) {
                                                                                    System.out.print("\r " + new Date() + " " + total);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.print("\r");

        if (sortedRosters.size() > 1) {
            sortedRosters.sort(Comparator.comparing(Roster::getPoints).reversed());
            sortedRosters.subList(numberToReturn, sortedRosters.size()).clear();
        }
        sortedRosters.forEach(Roster::sort);

        return sortedRosters;
    }
}