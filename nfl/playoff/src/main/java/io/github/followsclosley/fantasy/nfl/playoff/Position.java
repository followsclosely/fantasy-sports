package io.github.followsclosley.fantasy.nfl.playoff;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Represents a position on a roster. A position can contain multiple player positions.
 */
public class Position implements Predicate<Player> {

    private final String name;
    private final Set<String> positions;

    /**
     * Creates a new Position
     *
     * @param position The first string is the name of this position, followed by
     *                 the valid player positions that this Position can be filled by
     */
    public Position(String... position) {
        assert position != null;
        assert position.length > 0;

        this.name = position[0];
        if (position.length == 1) {
            this.positions = Set.copyOf(new HashSet<>(Arrays.asList(position)));
        } else {
            this.positions = Set.copyOf(new HashSet<>(Arrays.asList(position).subList(1, position.length)));
        }
    }

    /**
     * Returns the name of this position
     *
     * @return The name of this position
     */
    public String getName() {
        return name;
    }

    public Set<String> getPositions() {
        return positions;
    }

    @Override
    public boolean test(Player player) {
        return positions.contains(player.getPosition());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return name.equals(position.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
