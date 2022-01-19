package io.github.followsclosley.fantasy.nfl.playoff;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Roster {
    private float points;
    private List<Player> players = new ArrayList<>();

    public boolean canAddPlayer(Player player, RosterSettings rosterSettings) {

        // only one player per team
        for (Player playerOnRoster : players) {
            if (player.getTeam().equals(playerOnRoster.getTeam())) {
                return false;
            }
        }

        if (rosterSettings != null) {
            // limit the number per position
            int limit = rosterSettings.getLimit(player.getPosition());
            for (Player playerOnRoster : players) {
                if (player.getPosition().equals(playerOnRoster.getPosition())) {
                    if (--limit == 0) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public Roster addPlayer(Player player) {
        points += player.getPoints();
        players.add(player);
        return this;
    }

    public Roster orderPlayers() {
        this.players.sort(Comparator.comparingDouble(Player::getPoints).reversed());
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Points: ").append(String.format("%.2f", points)).append("\t");

        List<Player> players = new ArrayList<>(this.players);
        for (Player player : players) {
            builder.append(player.getName()).append(" (").append(player.getPoints()).append(")\t");
        }

        return builder.toString();
    }

    public String getUniqueKey() {
        //Sort the players so duplicate rosters are eliminated.
        players.sort(Comparator.comparing(Player::getId));

        // Generate a new hashCode
        StringBuilder builder = new StringBuilder();
        new ArrayList<Player>(players).stream().sorted(Comparator.comparing(Player::getId)).forEach(p->builder.append(p.getId()).append("."));

        return builder.toString();
    }

    public Roster getCopy() {
        Roster roster = new Roster();
        roster.players = new ArrayList<>(players);
        roster.points = points;
        return roster;
    }

    public float getPoints() {
        return points;
    }

    public List<Player> getPlayers() {
        return players;
    }
}