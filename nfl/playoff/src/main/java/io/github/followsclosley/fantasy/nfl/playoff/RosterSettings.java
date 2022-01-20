package io.github.followsclosley.fantasy.nfl.playoff;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains all the settings needed to generate an optimal roster.
 */
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "fantasy.nfl.roster")
public class RosterSettings {
    private int size;
    private Map<Position, Integer> limits;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Integer getLimit(Position position) {
        return limits.get(position);
    }

    public Set<Position> getPositions() {
        return limits.keySet();
    }

    public void setPositions(Map<String, Integer> config) {
        //Use a LinkedHashMap to maintain order.
        this.limits = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : config.entrySet()) {
            this.limits.put(new Position(entry.getKey().split(",")), entry.getValue());
        }
    }
}