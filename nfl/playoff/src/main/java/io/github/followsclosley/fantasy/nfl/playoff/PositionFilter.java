package io.github.followsclosley.fantasy.nfl.playoff;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class PositionFilter implements Predicate<Player> {
    private final Set<String> positions;

    public PositionFilter(String... position) {
        this.positions = new HashSet<>(Arrays.asList(position));
    }

    @Override
    public boolean test(Player player) {
        return positions.contains(player.getPosition());
    }
}
