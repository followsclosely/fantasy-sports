package io.github.followsclosley.fantasy.nfl.playoff;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "command.line.runner.enabled=false"
})
class RosterTest {

    Player te1 = new Player("te1", "TE1", 10, "TE", "Team0");
    Player te2 = new Player("te2", "TE2", 10, "TE", "Team1");
    Player te3 = new Player("te3", "TE3", 10, "TE", "Team2");

    Player wr1 = new Player("wr1", "WR1", 10, "WR", "Team3");
    Player wr2 = new Player("wr2", "WR2", 10, "WR", "Team4");
    Player wr3 = new Player("wr3", "WR3", 10, "WR", "Team5");

    @Autowired
    private RosterSettings rosterSettings;

    @Test
    void canAddPlayerSecondTeInFxSpot() {
        Roster roster = new Roster(rosterSettings);

        assertTrue(roster.addPlayer(te1).isPresent());
        assertTrue(roster.addPlayer(te2).isPresent());
    }

    @Test
    void canAddPlayerSecondTeInFxSpotAndTwoWrs() {
        Roster roster = new Roster(rosterSettings);

        assertTrue(roster.addPlayer(wr1).isPresent());
        assertTrue(roster.addPlayer(wr2).isPresent());

        assertTrue(roster.addPlayer(te1).isPresent());
        assertTrue(roster.addPlayer(te2).isPresent());
        assertFalse(roster.addPlayer(te3).isPresent());

        assertFalse(roster.addPlayer(wr3).isPresent());
    }
}