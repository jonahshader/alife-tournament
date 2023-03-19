package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.environment.EnvProperties;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.common.ChunkPerformance;
import com.csi4999.systems.networking.packets.RequestTournamentPacket;
import com.csi4999.systems.networking.packets.TournamentFailPacket;
import com.csi4999.systems.networking.packets.TournamentPacket;
import com.csi4999.systems.networking.packets.TournamentResultsPacket;
import com.csi4999.systems.networking.wrappers.Chunk;
import com.csi4999.systems.tournament.FuseEnvs;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;

public class TournamentListener implements Listener {

    private Database db;

    private Kryo k;

    private final String SAVE_CHUNK_QUERY = "INSERT INTO chunk(chunk_wins, chunk_confidence, user_id) VALUES (?,?,?);";
    private final String GET_TOURNAMENT_PARTICIPANTS_QUERY = "SELECT chunk_id FROM chunk WHERE user_id != ? ORDER BY RANDOM() LIMIT 3;";

    private final String CREATE_TOURNAMENT_QUERY = "INSERT INTO tournament(tournament_date) VALUES (?);";

    private final String INSERT_INTO_BRIDGE_TABLE = "INSERT INTO chunk_tourney(chunk_id, tournament_id) VALUES (?,?);";

    private final String GET_CURRENT_PERFORMANCE_QUERY = "SELECT chunk_wins FROM chunk WHERE chunk_id = ?;";

    private final String UPDATE_WINS_QUERY = "UPDATE chunk set chunk_wins = ? WHERE chunk_id = ?;";

    private EnvProperties tournamentProperties;

    public TournamentListener(Database db, Kryo k) {
        this.db = db;
        this.k = k;
        tournamentProperties = new EnvProperties();
        tournamentProperties.globalMutationRate = 0.0f;
        tournamentProperties.initialCreatures = 0;
        tournamentProperties.initialFood *= 2;
    }

    @Override
    public void received(Connection c, Object o) {

        if (o instanceof RequestTournamentPacket) {
            System.out.println("Received request for tournament");
            RequestTournamentPacket p = (RequestTournamentPacket) o;


            ArrayList<Chunk> participants = getParticipants(p.chunk.environment.userID);

            saveChunk(p.chunk);

            participants.add(p.chunk);

            if (participants.size() != 4) {
                // there are not enough chunks in the db to create a tournament
                c.sendTCP(new TournamentFailPacket());
            }
            else {
                Environment tournamentEnvironment = new Environment(tournamentProperties);
                ArrayList<Environment> participantEnvironments = new ArrayList<>();
                ArrayList<Long> chunkIDs = new ArrayList<>();

                for (Chunk chunk : participants) {
                    participantEnvironments.add(chunk.environment);
                    chunkIDs.add(chunk.chunkID);
                }

                FuseEnvs.fuseEnvs(participantEnvironments, tournamentEnvironment);

                c.sendTCP(new TournamentPacket(tournamentEnvironment, chunkIDs, p.chunk.environment.userID));
            }
        }
        else if (o instanceof TournamentResultsPacket) {
            System.out.println("Received tournament results");
            TournamentResultsPacket p = (TournamentResultsPacket) o;

            long tournamentID = createTournamentEntry();
            System.out.println("Logged new tournament with id: " + tournamentID);

            System.out.println("Linking tournament to participants");
            for (ChunkPerformance performance : p.performances){
                linkTournament(performance.chunkID, tournamentID);
            }

            System.out.println("Updating chunk performance");
            for (ChunkPerformance performance : p.performances) {
                updateChunkStatistics(performance);
            }
        }

    }

    private ArrayList<Chunk> getParticipants(long user_id) {

        try {
            ArrayList<Chunk> participants = new ArrayList<>();

            PreparedStatement statement = db.con.prepareStatement(GET_TOURNAMENT_PARTICIPANTS_QUERY);
            statement.setLong(1, user_id);

            ResultSet r = statement.executeQuery();

            while (r.next()) {
                long participantID = r.getLong("chunk_id");

                Chunk chunk = (Chunk) db.retrieveSerializedObject(SerializedType.CHUNK, participantID, k);
                participants.add(chunk);

                for (Creature c : chunk.environment.creatureSpawner.getCreatures())
                    System.out.println(c.chunkID);
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

        // assign chunkID to all creatures in chunk
        for (Creature c : chunk.environment.creatureSpawner.getCreatures())
            c.chunkID = chunk.chunkID;

        db.serializeObject(SerializedType.CHUNK, chunk.chunkID, k, chunk);
    }


    private long createTournamentEntry() {
        try {
            PreparedStatement preparedStatement = db.con.prepareStatement(CREATE_TOURNAMENT_QUERY, new String[] {"tournament_id"});
            preparedStatement.setString(1, LocalDate.now().toString());
            preparedStatement.executeUpdate();

            ResultSet key = preparedStatement.getGeneratedKeys();
            key.next();
            return key.getLong(1);
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void linkTournament(long chunkID, long tournamentID) {
        try{
            PreparedStatement preparedStatement = db.con.prepareStatement(INSERT_INTO_BRIDGE_TABLE);
            preparedStatement.setLong(1, chunkID);
            preparedStatement.setLong(2, tournamentID);
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void updateChunkStatistics(ChunkPerformance performance) {
        try {
            PreparedStatement preparedStatement =  db.con.prepareStatement(GET_CURRENT_PERFORMANCE_QUERY);
            preparedStatement.setLong(1, performance.chunkID);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            float currentWins = resultSet.getFloat("chunk_wins");

            currentWins += performance.proportionOfWin;

            preparedStatement = db.con.prepareStatement(UPDATE_WINS_QUERY);
            preparedStatement.setFloat(1, currentWins);
            preparedStatement.setLong(2, performance.chunkID);
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

    }



}
