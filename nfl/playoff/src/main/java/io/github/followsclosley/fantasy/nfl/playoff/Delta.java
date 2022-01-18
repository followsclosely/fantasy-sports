package io.github.followsclosley.fantasy.nfl.playoff;

public class Delta implements Comparable<Delta> {
    private final Player player;
    private float value;
    private Player nextBest;

    public Delta(Player player) {
        this.player = player;
    }

    public float getValue() {
        return value;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getNextBest() {
        return nextBest;
    }

    public void setNextBest(Player nextBest) {
        this.nextBest = nextBest;
        this.value = player.getPoints() - nextBest.getPoints();
    }

    @Override
    public int compareTo(Delta delta) {
        return Float.compare(delta.getValue(), value);
    }
}