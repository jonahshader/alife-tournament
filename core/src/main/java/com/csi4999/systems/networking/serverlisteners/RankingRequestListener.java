package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.RankingComparator;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.common.RankingInfo;
import com.csi4999.systems.networking.packets.RankingPacket;
import com.csi4999.systems.networking.packets.RequestRankingsPacket;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class RankingRequestListener implements Listener {

    private Database db;
    private Kryo k;

    private final String GET_USERS_QUERY = "SELECT user_id, username FROM user;";

    public RankingRequestListener(Database db, Kryo k) {
        this.db = db;
        this.k = k;
    }

    @Override
    public void received(Connection c, Object o) {

        if (o instanceof RequestRankingsPacket) {
            ArrayList<RankingInfo> leaderboard = getLeaderboard();

            c.sendTCP(new RankingPacket(leaderboard));
        }
    }

    private ArrayList<RankingInfo> getLeaderboard() {
        ArrayList<RankingInfo> leaderboard = new ArrayList<>();
        PriorityQueue<RankingInfo> rankings = new PriorityQueue<>(new RankingComparator());
        try {
            Statement s = db.con.createStatement();
            ResultSet r = s.executeQuery(GET_USERS_QUERY);

            while (r.next()) {
                String username = r.getString("username");
                long id = r.getLong("user_id");
                UserAccountPacket u = (UserAccountPacket) db.retrieveSerializedObject(SerializedType.USER_ACCOUNT, id, k);

                rankings.add(new RankingInfo(u.rank, username));

            }

            while ((! rankings.isEmpty()) && (leaderboard.size() <= 10)) {
                leaderboard.add(rankings.poll());
            }


            return leaderboard;

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
