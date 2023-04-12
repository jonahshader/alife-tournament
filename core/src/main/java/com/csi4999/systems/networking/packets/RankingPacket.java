package com.csi4999.systems.networking.packets;

import com.csi4999.systems.networking.common.RankingInfo;

import java.util.ArrayList;

public class RankingPacket {

    public ArrayList<RankingInfo> leaderboard;

    public RankingPacket(){};

    public RankingPacket(ArrayList<RankingInfo> r){this.leaderboard = r;}
}
