package io.github.followsclosley.fantasy.nfl.playoff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * This PlayerPoolLoader loads the first player for each position per team.
 * Assumes the player file is sorted and in the following format:
 * <pre>
 *   <b>PlayerName,Team,Position,Points</b>
 *   Tom Brady,NE,QB,58.71
 * </pre>
 * <p>
 *
 * @author M.L. Wilson
 */
@Configuration
public class PlayerPoolLoader {

    @Value("${fantasy.nfl.stats-resource}")
    private final Resource resource = null;

    @Value("${fantasy.nfl.excluded-teams:#{null}}")
    private List<String> excludedTeams;

    @Autowired
    private RosterSettings rosterSettings;

    @Bean
    public PlayerPool load() throws IOException {
        PlayerPool pool = new PlayerPool();
        int i = 0;
        String line;

        //Load all the players
        List<Player> allPlayers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            // File must be sorted by most fantasy points descending
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) // Comment out players with a #
                {
                    String[] values = line.split(",");

                    //String id, String name, float points, String position, String team
                    //2022,Patrick Mahomes,KC,QB,47.06
                    int year = Integer.parseInt(values[0]);

                    if (year == 2025) {
                        allPlayers.add(new Player(String.valueOf(i++), values[1], Float.parseFloat(values[4]), values[3], values[2]));
                    }
                }
            }
        }

        Set<String> lock = new HashSet<>();
        //Sort by points, so we have the best players first.
        allPlayers.sort(Comparator.comparingDouble(Player::getPoints).reversed());
        for (Player player : allPlayers) {
            //Only add one player per position, per team as we don't care about
            //the second-best player on a team at a given position
            String key = player.getPosition() + player.getTeam();
            if (lock.add(key)) {
                //We do need a player from every team, even if the player scores negative points.
                //It is unlikely that the player would be selected, but possible.
                pool.addPlayer(player);
            }

        }

        return pool;
    }
}