package io.github.followsclosley.fantasy.nfl.playoff;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Holds all players, grouped by position.
 *
 * @author M.L Wilson
 */
public class PlayerPool {
    private final Map<String, List<Player>> pool = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        public List<Player> get(Object key) {
            List<Player> players = super.get(key);
            if (players == null) {
                super.put((String) key, players = new ArrayList<>());
            }
            return players;
        }
    };

    public Set<String> getPositions() {
        return pool.keySet();
    }

    public List<Player> getPlayers(String position) {
        return pool.get(position);
    }

    /**
     * This method prunes the player pool so that it has just one player per team per position.
     *
     * @return an unmodifiable instance of PlayerPool
     */
    public PlayerPool finalizePool(){
        PlayerPool clone = new PlayerPool();

        for (String position : pool.keySet()) {
            List<Player> players = pool.get(position);

            List<Player> maxByPoints = players.stream()
                    .collect(Collectors.groupingBy(
                            Player::getTeam,
                            Collectors.maxBy(Comparator.comparingDouble(Player::getPoints))
                    ))
                    .values() // Collection<Optional<Person>>
                    .stream() // Stream<Optional<Person>>
                    .map(opt -> opt.orElse(null)).sorted(Comparator.comparingDouble(Player::getPoints).reversed()).collect(Collectors.toList());

            clone.pool.put(position, Collections.unmodifiableList(maxByPoints));
        }

        return clone;
    }
}