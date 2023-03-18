package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.packets.RequestTournamentPacket;
import com.csi4999.systems.networking.packets.TournamentFailPacket;
import com.csi4999.systems.networking.wrappers.Chunk;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TournamentListener implements Listener {

    private Database db;

    private Kryo k;

    private final String SAVE_CHUNK_QUERY = "INSERT INTO chunk(chunk_wins, chunk_confidence, user_id) VALUES (?,?,?);";
    private final String GET_TOURNAMENT_PARTICIPANTS_QUERY = "SELECT chunk_id FROM chunk ORDER BY RANDOM() LIMIT 3;";



    public TournamentListener(Database db, Kryo k) {
        this.db = db;
        this.k = k;
    }

    @Override
    public void received(Connection c, Object o) {

        if (o instanceof RequestTournamentPacket) {
            RequestTournamentPacket p = (RequestTournamentPacket) o;


            ArrayList<Chunk> participants = getParticipants();

            saveChunk(p.chunk);

            participants.add(p.chunk);

            if (participants.size() != 4) {
                // there are not enough chunks in the db to create a tournament
                c.sendTCP(new TournamentFailPacket());
            }
            else {
                /* TODO generate tournament packet out of the participants list*/
            }
        }

    }

    private ArrayList<Chunk> getParticipants() {
        try {
            ArrayList<Chunk> participants = new ArrayList<>();
            Statement stmt = db.con.createStatement();
            ResultSet r = stmt.executeQuery(GET_TOURNAMENT_PARTICIPANTS_QUERY);

            while (r.next()) {
                long participantID = r.getLong("chunk_id");

                Chunk chunk = (Chunk) db.retrieveSerializedObject(SerializedType.CHUNK, participantID, k);
                participants.add(chunk);
            }

            return participants;
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void saveChunk(Chunk chunk) {

        try {
            PreparedStatement preparedStatement = db.con.prepareStatement(SAVE_CHUNK_QUERY, new String[] {"chunk_id"});
            preparedStatement.setFloat(1, 0.0f);
            preparedStatement.setFloat(2, 0.0f);
            preparedStatement.setLong(3, chunk.environment.userID);
            preparedStatement.executeUpdate();

            ResultSet key = preparedStatement.getGeneratedKeys();
            key.next();
            chunk.chunkID = key.getLong(1);
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        db.serializeObject(SerializedType.CHUNK, chunk.chunkID, k, chunk);
    }
}
