package io.github.followsclosley.fantasy.nfl.playoff;

import java.util.*;

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
}