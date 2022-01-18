package io.github.followsclosley.fantasy.nfl.playoff.lineup;

import io.github.followsclosley.fantasy.nfl.playoff.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SimpleLineupGenerator implements LineupGenerator {
    //12QB*11RB*10RB*9WR*8WR*7WR*6TE*5DT*4K*(only 80% of the lineups are unique)
    double estimatedTotal = 12 * 11 * 10 * 9 * 8 * 7 * 6 * 5 * 4 * .8;
    private int numberToRetrun = 1;

    public void setNumberToRetrun(int numberToRetrun) {
        this.numberToRetrun = numberToRetrun;
    }

    public void setEstimatedTotal(double estimatedTotal) {
        this.estimatedTotal = estimatedTotal;
    }

    public ArrayList<Roster> generate(PlayerPool pool, RosterSettings rosterSettings) {
        int total = 0;

        List<Player> fxs = new ArrayList<>(pool.getPlayers("WR"));
        //List<Player> fxs = new ArrayList<>(pool.getPlayers("RB"));
        //List<Player> fxs = new ArrayList<>(pool.getPlayers("TE"));

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
                                                                                Collections.sort(sortedRosters);
                                                                                //Only keep the top {numberToRetrun}.
                                                                                sortedRosters.subList(numberToRetrun, sortedRosters.size()).clear();
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

        Collections.sort(sortedRosters);
        sortedRosters.subList(numberToRetrun, sortedRosters.size()).clear();
        System.out.format("%s %.2f%% %d %n", new Date(), ((total / estimatedTotal) * 100), total);

        return sortedRosters;
    }
}