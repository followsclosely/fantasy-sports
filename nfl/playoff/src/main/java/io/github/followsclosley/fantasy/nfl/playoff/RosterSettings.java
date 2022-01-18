package io.github.followsclosley.fantasy.nfl.playoff;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "fantasy.nfl.roster")
public class RosterSettings {
    private int size;
    private Map<String, Integer> limits;

    public void setLimits(Map<String, Integer> limits) {
        this.limits = limits;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Integer getLimit(String position) {
        return limits.get(position);
    }

    public Set<String> getPositions() {
        return limits.keySet();
    }
}