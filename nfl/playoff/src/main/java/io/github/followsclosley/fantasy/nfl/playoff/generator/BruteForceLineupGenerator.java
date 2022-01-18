package io.github.followsclosley.fantasy.nfl.playoff.generator;

import io.github.followsclosley.fantasy.nfl.playoff.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This RosterGenerator uses brute force and generates all possible lineups for the
 * following RosterSettings:
 * <ul>
 *   <li>QB: 1</li>
 *   <li>RB: 2</li>
 *   <li>WR: 2</li>
 *   <li>TE: 2</li>
 *   <li>K: 1</li>
 *   <li>D: 1</li>
 * </ul>
 * <p>
 * Note that this implementation does not use the RosterSettings passed in. It is hard coded
 * to the above 9 positions.
 */
@Component
public class BruteForceLineupGenerator implements RosterGenerator {

    @Value("${fantasy.nfl.roster-generator.brute-force.number-to-return:1}")
    private final int numberToReturn = 1;
    @Value("${fantasy.nfl.roster-generator.brute-force.debug:false}")
    private final boolean debug = false;
    //9QB*8RB*7RB*6WR*6WR*6WR*6TE*3DT*3K*(only 75% of the lineups are unique)
    double estimatedTotal = 9 * 8 * 7 * 6 * 6 * 6 * 6 * 3 * 3 * .75;

    public ArrayList<Roster> generate(PlayerPool pool, RosterSettings rosterSettings) {
        int total = 0;

        List<Player> fxs = new ArrayList<>(pool.getPlayers("WR"));

        ArrayList<Roster> sortedRosters = new ArrayList<>();
        Set<String> unique = new HashSet<>();

        for (Player qb : pool.getPlayers("QB")) {
            Roster roster = new Roster().addPlayer(qb);
            for (Player rb1 : pool.getPlayers("RB")) {
                if (roster.canAddPlayer(rb1, null)) {
                    Roster rosterRb1 = roster.getCopy().addPlayer(rb1);
                    for (Player rb2 : pool.getPlayers("RB")) {
                        if (rosterRb1.canAddPlayer(rb2, null)) {
                            Roster rosterRb2 = rosterRb1.getCopy().addPlayer(rb2);
                            for (Player wr1 : pool.getPlayers("WR")) {
                                if (rosterRb2.canAddPlayer(wr1, null)) {
                                    Roster rosterWr1 = rosterRb2.getCopy().addPlayer(wr1);
                                    for (Player wr2 : pool.getPlayers("WR")) {
                                        if (rosterWr1.canAddPlayer(wr2, null)) {
                                            Roster rosterWr2 = rosterWr1.getCopy().addPlayer(wr2);
                                            for (Player te : pool.getPlayers("TE")) {
                                                if (rosterWr2.canAddPlayer(te, null)) {
                                                    Roster rosterTe = rosterWr2.getCopy().addPlayer(te);
                                                    for (Player pk : pool.getPlayers("K")) {
                                                        if (rosterTe.canAddPlayer(pk, null)) {
                                                            Roster rosterPk = rosterTe.getCopy().addPlayer(pk);
                                                            for (Player dt : pool.getPlayers("D")) {
                                                                if (rosterPk.canAddPlayer(dt, null)) {
                                                                    Roster rosterDt = rosterPk.getCopy().addPlayer(dt);
                                                                    for (Player fx : fxs) {
                                                                        if (rosterDt.canAddPlayer(fx, null)) {
                                                                            Roster rosterFx = rosterDt.getCopy().addPlayer(fx);
                                                                            if (unique.add(rosterFx.getUniqueKey())) {
                                                                                sortedRosters.add(rosterFx);
                                                                            }

                                                                            if (++total % 1000000 == 0) {
                                                                                sortedRosters.sort(Roster::sortByPoints);
                                                                                sortedRosters.subList(numberToReturn, sortedRosters.size()).clear();
                                                                                if (debug) {
                                                                                    System.out.print(String.format("\r  %s %.2f%% %d %n", new Date(), ((total / estimatedTotal) * 100), total));
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

        sortedRosters.sort(Roster::sortByPoints);
        sortedRosters.subList(numberToReturn, sortedRosters.size()).clear();
        sortedRosters.forEach(Roster::orderPlayers);

        if (debug) {
            System.out.format("%s %.2f%% %d %n", new Date(), ((total / estimatedTotal) * 100), total);
        }

        return sortedRosters;
    }
}