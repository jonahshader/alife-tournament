package com.csi4999.systems.networking.clientListeners;

import com.csi4999.systems.networking.common.RankingInfo;
import com.csi4999.systems.networking.packets.RankingPacket;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.util.ArrayList;

public class RankingResponseListener implements Listener {

    private static RankingResponseListener instance;

    public volatile boolean ready;

    public ArrayList<RankingInfo> leaderboard;

    public static RankingResponseListener getInstance() {
        if (instance == null) {
            instance = new RankingResponseListener();
        }
        return instance;
    }

    private RankingResponseListener() {
        ready = false;
    }

    @Override
    public void received(Connection c, Object o) {

        if (o instanceof RankingPacket) {
            System.out.println("Got Ranking Packet");
            ready = false;

            this.leaderboard = ((RankingPacket) o).leaderboard;

            ready = true;
        }
    }
}
