package io.github.followsclosley.fantasy.nfl.playoff;

import java.util.List;

public interface LineupGenerator {
    List<Roster> generate(PlayerPool pool, RosterSettings rosterSettings);
}