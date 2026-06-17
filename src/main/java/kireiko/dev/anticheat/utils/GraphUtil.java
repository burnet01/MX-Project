package kireiko.dev.anticheat.utils;

import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public final class GraphUtil {

    public static NamedTextColor getColorForPing(long ping) {
        if (ping > 1000) return NamedTextColor.DARK_RED;
        if (ping > 300) return NamedTextColor.RED;
        if (ping > 100) return NamedTextColor.YELLOW;
        return NamedTextColor.GREEN;
    }

    public static class GraphResult {
        private final int positives;
        private final int negatives;

        public GraphResult(int positives, int negatives) {
            this.positives = positives;
            this.negatives = negatives;
        }

        public int getPositives() { return positives; }
        public int getNegatives() { return negatives; }
    }

    public static GraphResult getGraph(List<Double> samples) {
        int positives = 0;
        int negatives = 0;
        for (int i = 1; i < samples.size(); i++) {
            double diff = samples.get(i) - samples.get(i - 1);
            if (diff > 0) positives++;
            else if (diff < 0) negatives++;
        }
        return new GraphResult(positives, negatives);
    }
}
