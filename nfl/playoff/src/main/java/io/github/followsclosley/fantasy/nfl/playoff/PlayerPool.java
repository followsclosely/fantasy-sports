package io.github.followsclosley.fantasy.nfl.playoff;

import java.util.*;
import java.util.stream.Collectors;
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

        //If there is more than one position:
        // 1) Group by team
        // 2) select the max points scored
        // 3) Sort by points scored
        if( position.getPositions().size() > 1){
            stream = stream.collect(Collectors.groupingBy(Player::getTeam, Collectors.maxBy(Comparator.comparingDouble(Player::getPoints))))
                    .values().stream().flatMap(Optional::stream)
                    .sorted(Comparator.comparingDouble(Player::getPoints).reversed());
        }

        return stream;
    }
}