package com.csi4999.systems.networking;

import com.csi4999.systems.networking.common.RankingInfo;

import java.util.Comparator;

public class RankingComparator implements Comparator<RankingInfo> {
    @Override
    public int compare(RankingInfo o1, RankingInfo o2) {

        if (o1.ranking < o2.ranking)
            return 1;
        else if (o1.ranking > o2.ranking)
            return -1;

        return 0;
    }
}
