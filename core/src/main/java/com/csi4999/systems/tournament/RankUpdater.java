package com.csi4999.systems.tournament;

import com.csi4999.systems.CustomMath;

import java.util.ArrayList;
import java.util.List;

public class RankUpdater {
    public static final float RANK_MEAN = 50.0f;
    public static final float RANK_STD = 20.0f;
    public static final float RANK_CHANGE_MIN_PROPORTION = 0.25f;

    private static final float SMALL = 0.001f;

    private static List<Float> calculateNewTargetRank(List<Float> ranks, List<Float> performances) {
        float meanRank = 0;
        for (Float rank : ranks)
            meanRank += rank;
        meanRank /= ranks.size();

        List<Float> newRanks = new ArrayList<>();
        for (int i = 0; i < ranks.size(); i++) {
//            float currentRank = ranks.get(i);
            float currentPerformance = performances.get(i);
            // put some limits on this so that we don't break normalCDFInverse
            float sqrtCurrentPerformance = (float) Math.sqrt(currentPerformance);
            if (sqrtCurrentPerformance < SMALL) sqrtCurrentPerformance = SMALL;
            if (sqrtCurrentPerformance > 1 - SMALL) sqrtCurrentPerformance = 1 - SMALL;

//            float expectedPerformance = (float) Math.pow(CustomMath.CNDF((currentRank - meanRank) / RANK_STD), 2);
            float expectedRank = (float) (RANK_STD * CustomMath.normalCDFInverse(sqrtCurrentPerformance) + meanRank);
            newRanks.add(expectedRank);
        }
        return newRanks;
    }

    public static List<Float> calculateNewRank(List<Float> ranks, List<Integer> gamesPlayed, List<Float> performances) {
        List<Float> newRanks = calculateNewTargetRank(ranks, performances);

        for (int i = 0; i < newRanks.size(); i++) {
            float proportionNew = 1.0f / gamesPlayed.get(i);
            proportionNew = Math.max(proportionNew, RANK_CHANGE_MIN_PROPORTION);
            newRanks.set(i, (newRanks.get(i) * proportionNew) + (ranks.get(i) * (1-proportionNew)));
        }

        return newRanks;
    }
}
