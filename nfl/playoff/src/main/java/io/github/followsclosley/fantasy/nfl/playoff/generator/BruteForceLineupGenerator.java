package io.github.followsclosley.fantasy.nfl.playoff.generator;

import io.github.followsclosley.fantasy.nfl.playoff.*;
import org.springframework.beans.factory.annotation.Value;
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
@Component
public class BruteForceLineupGenerator implements RosterGenerator {

    @Value("${fantasy.nfl.roster-generator.brute-force.number-to-return:1}")
    private final int numberToReturn = 1;

    @Value("${fantasy.nfl.roster-generator.brute-force.debug:false}")
    private final boolean debug = false;

    //9QB*8RB*7RB*6WR*6WR*6WR*6TE*3DT*3K*(only 75% of the lineups are unique)
    double estimatedTotal = 9 * 8 * 7 * 6 * 6 * 6 * 6 * 3 * 3 * .75;

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
            Roster roster = new Roster().addPlayer(qb);
            for (Player rb1 : rbs) {
                if (roster.canAddPlayer(rb1, null)) {
                    Roster rosterRb1 = roster.getCopy().addPlayer(rb1);
                    for (Player rb2 : rbs) {
                        if (rosterRb1.canAddPlayer(rb2, null)) {
                            Roster rosterRb2 = rosterRb1.getCopy().addPlayer(rb2);
                            for (Player wr1 : wrs) {
                                if (rosterRb2.canAddPlayer(wr1, null)) {
                                    Roster rosterWr1 = rosterRb2.getCopy().addPlayer(wr1);
                                    for (Player wr2 : wrs) {
                                        if (rosterWr1.canAddPlayer(wr2, null)) {
                                            Roster rosterWr2 = rosterWr1.getCopy().addPlayer(wr2);
                                            for (Player te : tes) {
                                                if (rosterWr2.canAddPlayer(te, null)) {
                                                    Roster rosterTe = rosterWr2.getCopy().addPlayer(te);
                                                    for (Player pk : ks) {
                                                        if (rosterTe.canAddPlayer(pk, null)) {
                                                            Roster rosterPk = rosterTe.getCopy().addPlayer(pk);
                                                            for (Player dt : ds) {
                                                                if (rosterPk.canAddPlayer(dt, null)) {
                                                                    Roster rosterDt = rosterPk.getCopy().addPlayer(dt);
                                                                    for (Player fx : fxs) {
                                                                        if (rosterDt.canAddPlayer(fx, null)) {
                                                                            Roster rosterFx = rosterDt.getCopy().addPlayer(fx);
                                                                            if (unique.add(rosterFx.getUniqueKey())) {
                                                                                sortedRosters.add(rosterFx);
                                                                            }

                                                                            if (++total % 1000000 == 0) {
                                                                                sortedRosters.sort(Comparator.comparing(Roster::getPoints).reversed());
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

        if (sortedRosters.size() > 1) {
            sortedRosters.sort(Comparator.comparing(Roster::getPoints).reversed());
            sortedRosters.subList(numberToReturn, sortedRosters.size()).clear();
        }
        sortedRosters.forEach(Roster::orderPlayers);

        if (debug) {
            System.out.format("%s %.2f%% %d %n", new Date(), ((total / estimatedTotal) * 100), total);
        }

        return sortedRosters;
    }
}