package io.github.followsclosley.fantasy.nfl.playoff;

/**
 * Player pojo
 *
 * @author M.L. Wilson
 */
public class Player implements Comparable<Player> {
    private final String id;
    private final String name;
    private final float points;
    private final String position;
    private final String team;

    public Player(String id, String name, float points, String position, String team) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.position = position;
        this.team = team;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Player player) {
        return player.getId().compareTo(id);
    }

    public String getName() {
        return name;
    }

    public float getPoints() {
        return points;
    }

    public String getPosition() {
        return position;
    }

    public String getTeam() {
        return team;
    }

    public String getId() {
        return id;
    }
}
