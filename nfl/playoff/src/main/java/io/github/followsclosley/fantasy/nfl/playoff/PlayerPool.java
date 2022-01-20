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

    private final List<Player> pool = new ArrayList<>();

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(this.pool);
    }

    protected void addPlayer(Player player) {
        pool.add(player);
    }

    public Stream<Player> getPlayers(String... position) {
        Position predicate = new Position(position);
        return getPlayers(predicate);
    }

    public Stream<Player> getPlayers(Position position) {
        Stream<Player> stream = pool.stream().filter(position);
        return (position.getPositions().size() == 1) ? stream : stream.sorted(Comparator.comparingDouble(Player::getPoints).reversed());
    }
}