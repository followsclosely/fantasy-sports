package io.github.followsclosley.fantasy.nfl.playoff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This PlayerPoolLoader loads the first player for each position per team.
 * Assumes the player file is sorted and in the following format:
 * <p><pre>
 *   <b>PlayerName,Team,Position,Points</b>
 *   Tom Brady,NE,QB,58.71
 * </pre></p>
 * <p>
 *
 * @author M.L. Wilson
 */
@Configuration
public class PlayerPoolLoader {
    @Value("${fantasy.nfl.stats-resource}")
    private final Resource resource = null;

    @Autowired
    private RosterSettings rosterSettings;

    @Bean
    public PlayerPool load() throws IOException {
        PlayerPool pool = new PlayerPool();
        int i = 0;
        String line;
        Set<String> lock = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            // File must be sorted by most fantasy points descending
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) // Comment out players with a #
                {
                    String[] values = line.split(",");
                    Player player = new Player(String.valueOf(i++), values[0], Float.parseFloat(values[3]), values[2], values[1]);

                    String key = player.getPosition() + player.getTeam();

                    // Only add the best player per team for every position.
                    if (!lock.contains(key)) {
                        int size = pool.getPlayers(player.getPosition()).size();
                        if (size < rosterSettings.getSize()) {
                            pool.getPlayers(player.getPosition()).add(player);
                            lock.add(key);
                        }
                    }
                }
            }
        }

        for (String position : pool.getPositions()) {
            List<Player> players = pool.getPlayers(position);
            players.sort(Player::sortByPoints);
        }

        return pool;
    }
}