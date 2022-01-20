package io.github.followsclosley.fantasy.nfl.playoff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Holds all players.
 *
 * @author M.L Wilson
 */
public class PlayerPool {

    private List<Player> pool = new ArrayList<>();

    public List<Player> getPlayers() {
        return pool;
    }

    public void addPlayer(Player player) {
        pool.add(player);
    }

    public Stream<Player> getPlayers(String... position) {
        PositionFilter predicate = new PositionFilter(position);
        Stream<Player> stream = pool.stream().filter(predicate);
        return (position.length == 1) ? stream : stream.sorted(Comparator.comparingDouble(Player::getPoints).reversed());
    }

    public PlayerPool lock() {
        this.pool = Collections.unmodifiableList(this.pool);
        return this;
    }
}