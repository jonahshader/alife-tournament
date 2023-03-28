package com.csi4999.systems.networking.clientListeners;

import com.csi4999.screens.SimScreen;
import com.csi4999.screens.TournamentWaitScreen;
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.packets.NewRanksPacket;
import com.csi4999.systems.networking.packets.TournamentFailPacket;
import com.csi4999.systems.networking.packets.TournamentPacket;
import com.csi4999.systems.tournament.RankUpdater;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import static com.csi4999.systems.tournament.RankUpdater.RANK_CHANGE_MIN_PROPORTION;

public class TournamentResponseListener implements Listener {

    private static TournamentResponseListener instance;

    public static TournamentResponseListener getInstance() {
        if (instance == null) {
            instance = new TournamentResponseListener();
        }
        return instance;
    }

    @Override
    public void received(Connection c, Object o) {

        if (o instanceof TournamentFailPacket) {
            System.out.println("Tournament Creation Failed");
            TournamentWaitScreen.instance.tournamentFailed = true;
        } else if (o instanceof TournamentPacket) {
            System.out.println("Received Tournament Packet!");
            // we are assuming that the TournamentWaitScreen has been created prior to this
            // give it the TournamentPacket and it will switch screens to the TournamentScreen/SimScreen
            // can't change screens here because we are not in the main thread
            TournamentWaitScreen.instance.tournamentPacket = (TournamentPacket) o;
        } else if (o instanceof NewRanksPacket) {
            System.out.println("Received NewRanksPacket");
            SimScreen.instance.newRanksPacket = (NewRanksPacket) o;

            float currentRank = GameClient.getInstance().user.rank;
            float nextRank = SimScreen.instance.newRanksPacket.ranks.get(0); // first one is always this user's chunk's rank
            int gamesPlayed = ++GameClient.getInstance().user.gamesPlayed;
            float proportion = Math.max(1f / gamesPlayed, RANK_CHANGE_MIN_PROPORTION);

            nextRank = nextRank * proportion + currentRank * (1-proportion);
            System.out.println("User's rank went from " + currentRank + " to " + nextRank);
            GameClient.getInstance().user.rank = nextRank;
            c.sendTCP(GameClient.getInstance().user);
        }
    }
}
