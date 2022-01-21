package io.github.followsclosley.fantasy.nfl.playoff;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds players that make up a starting lineup.
 *
 * <p>
 * Uses the <code>RosterSettings</code> to enforce a valid starting lineup
 * </p>
 */
public class Roster {

    private final RosterSettings rosterSettings;
    private final List<Player> players = new ArrayList<>();
    private final Map<Position, AtomicInteger> counts = new HashMap<>();
    private float points;

    public Roster(RosterSettings rosterSettings) {
        this.rosterSettings = rosterSettings;
        if (this.rosterSettings != null) {
            rosterSettings.getPositions().forEach(p -> counts.put(p, new AtomicInteger(0)));
        }
    }

    /**
     * Does this player exist on the current roster
     *
     * @param player The player in question
     * @return true if this player exist on the current roster
     */
    public boolean contains(Player player) {
        return players.contains(player);
    }

    /**
     * Gets the number of players for a given Position
     *
     * @param position The position in question
     * @return Gets the number of players for a given Position
     */
    public int getCount(Position position) {
        return counts.get(position).get();
    }

    /**
     * Answers if a given player can be added to a roster.
     *
     * @param player The player in question
     * @return true if the player can be added to the roster
     */
    public Optional<Position> canAddPlayer(Player player) {

        if (player == null) {
            return Optional.empty();
        }

        // only one player per team
        for (Player playerOnRoster : players) {
            if (player.getTeam().equals(playerOnRoster.getTeam())) {
                return Optional.empty();
            }
        }

        //Verify that there is room on the roster.
        if (rosterSettings != null) {
            for (Position position : rosterSettings.getPositions()) {
                if (position.test(player) && counts.get(position).get() < rosterSettings.getLimit(position)) {
                    return Optional.of(position);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Adds a player to the roster
     *
     * @param player the player to be added
     * @return The position the player was added as, empty if player was not added.
     */
    public Optional<Position> addPlayer(Player player) {
        Optional<Position> optionalPosition = canAddPlayer(player);

        if (optionalPosition.isPresent()) {
            points += player.getPoints();
            players.add(player);

            Position position = optionalPosition.get();
            counts.get(position).incrementAndGet();
        }

        return optionalPosition;
    }

    /**
     * Sorts all the players on the roster by points
     *
     * @return the sorted roster
     */
    public Roster sort() {
        this.players.sort(Comparator.comparingDouble(Player::getPoints).reversed());
        return this;
    }

    public boolean isFull() {
        return players.size() >= rosterSettings.getSize();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Points: ").append(String.format("%.2f", points)).append("\t");

        List<Player> players = new ArrayList<>(this.players);
        for (Player player : players) {
            builder.append(player.getName(), 0, Math.min(player.getName().length(), 15)).append(" (").append(player.getPoints()).append(") ");
        }

        return builder.toString();
    }

    public String toString(int nameLength) {
        StringBuilder builder = new StringBuilder();
        builder.append("Points: ").append(String.format("%.2f", points)).append("\t");

        List<Player> players = new ArrayList<>(this.players);
        for (Player player : players) {
            String[] names = player.getName().split(" ");
            String name = (names.length == 2) ? names[0].charAt(0) + ". " + names[1].substring(0, Math.min(names[1].length(), nameLength)) : names[0];
            builder.append(name).append(", ");
        }

        return builder.substring(0, builder.length() - 2);
    }

    /**
     * Generates a unique string that represents this roster.
     *
     * @return a unique key
     */
    public String getUniqueKey() {
        //Sort the players so duplicate rosters are eliminated.
        players.sort(Comparator.comparing(Player::getId));

        // Generate a new hashCode
        StringBuilder builder = new StringBuilder();
        for (Player p : players) {
            builder.append(p.getId()).append(".");
        }

        return builder.toString();
    }

    /**
     * Adds a player and clones the Roster.
     *
     * @return a clone of itself
     */
    public Roster addPlayerAndClone(Player player) {
        Roster roster = new Roster(rosterSettings);
        roster.players.addAll(players);
        roster.points = points;

        counts.forEach((key, value) -> roster.counts.put(key, new AtomicInteger(value.get())));
        roster.addPlayer(player);
        return roster;
    }

    /**
     * Getter for fantasy points scored by this roster.
     *
     * @return fantasy points scored by this roster
     */
    public float getPoints() {
        return points;
    }

}