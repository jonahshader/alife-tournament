package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.environment.EnvProperties;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.common.ChunkPerformance;
import com.csi4999.systems.networking.packets.*;
import com.csi4999.systems.networking.wrappers.Chunk;
import com.csi4999.systems.tournament.FuseEnvs;
import com.csi4999.systems.tournament.RankUpdater;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

import static com.csi4999.systems.tournament.RankUpdater.RANK_MEAN;

public class TournamentListener implements Listener {

    private Database db;

    private Kryo k;

    private final String SAVE_CHUNK_QUERY = "INSERT INTO chunk(games_played, rank, user_id) VALUES (?,?,?);";
    private final String GET_TOURNAMENT_PARTICIPANTS_QUERY = "SELECT chunk_id FROM chunk WHERE user_id != ? ORDER BY ABS(rank - ?) LIMIT 3;";

    private final String GET_USERNAME_FROM_CHUNK_ID_QUERY = "SELECT username FROM user INNER JOIN chunk ON user.user_id = chunk.user_id WHERE chunk_id = ?;";

    private final String CREATE_TOURNAMENT_QUERY = "INSERT INTO tournament(tournament_date) VALUES (?);";

    private final String INSERT_INTO_BRIDGE_TABLE = "INSERT INTO chunk_tourney(chunk_id, tournament_id) VALUES (?,?);";

//    private final String UPDATE_WINS_QUERY = "UPDATE chunk set games_played = ? WHERE chunk_id = ?;";

    private final String GET_DATA_FROM_CHUNK_ID = "SELECT rank, games_played FROM chunk WHERE chunk_id = ?;";

    private final String SET_DATA_FROM_CHUNK_ID = "UPDATE chunk SET games_played = ?, rank = ? WHERE chunk_id = ?;";

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

            // ordered so that requester's chunk is first
            List<Chunk> participants = new ArrayList<>();
            saveChunk(p.chunk, p.rank);
            participants.add(p.chunk);
            participants.addAll(getParticipants(p.chunk.environment.userID, p.rank));


            if (participants.size() != 4) {
                // there are not enough chunks in the db to create a tournament
                c.sendTCP(new TournamentFailPacket());
            }
            else {
                Environment tournamentEnvironment = new Environment(tournamentProperties);
                List<Environment> participantEnvironments = new ArrayList<>();
                List<Long> chunkIDs = new ArrayList<>();


                for (Chunk chunk : participants) {
                    participantEnvironments.add(chunk.environment);
                    chunkIDs.add(chunk.chunkID);
                }

                List<String> names = getChunkOwnerUsernames(chunkIDs);
                List<Float> initialRanks = getRanks(chunkIDs);

                FuseEnvs.fuseEnvs(participantEnvironments, tournamentEnvironment);

                c.sendTCP(new TournamentPacket(tournamentEnvironment, chunkIDs, initialRanks, names, p.chunk.environment.userID));
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
//            for (ChunkPerformance performance : p.performances) {
//                updateChunkStatistics(performance);
//            }
            updateRanks(p.performances, c);

            System.out.println("Sending out NewRanksPacket");

        }

    }

    private void updateRanks(List<ChunkPerformance> chunkPerformances, Connection c) {
        // ChunkPerformance has chunkID, performance (proportionOfWin)
        // in addition to this, we need games_played and rank from each chunk with chunkID

        List<Float> ranks = new ArrayList<>();
        List<Float> performances = new ArrayList<>();
        List<Integer> gamesPlayed = new ArrayList<>();


        try {
            // retrieve data
            for (ChunkPerformance cPerf : chunkPerformances) {
                PreparedStatement statement = db.con.prepareStatement(GET_DATA_FROM_CHUNK_ID);
                statement.setLong(1, cPerf.chunkID);

                ResultSet r = statement.executeQuery();

                ranks.add(r.getFloat("rank"));
                gamesPlayed.add(r.getInt("games_played") + 1);
                performances.add(cPerf.proportionOfWin);
            }

            // calculate new ranks
            List<Float> newRanks = RankUpdater.calculateNewRank(ranks, gamesPlayed, performances);
            List<Long> chunkIDs = new ArrayList<>();

            // save new ranks into db
            for (int i = 0; i < chunkPerformances.size(); i++) {
                long chunkID = chunkPerformances.get(i).chunkID;
                float rank = newRanks.get(i);
                long gPlayed = gamesPlayed.get(i);

                chunkIDs.add(chunkID);

                PreparedStatement statement = db.con.prepareStatement(SET_DATA_FROM_CHUNK_ID);
                statement.setLong(1, gPlayed);
                statement.setFloat(2, rank);
                statement.setLong(3, chunkID);

                statement.executeUpdate();
            }

            c.sendTCP(new NewRanksPacket(getRanks(chunkIDs)));
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

    }

    private ArrayList<Chunk> getParticipants(long user_id, float rank) {

        try {
            ArrayList<Chunk> participants = new ArrayList<>();

            PreparedStatement statement = db.con.prepareStatement(GET_TOURNAMENT_PARTICIPANTS_QUERY);
            statement.setLong(1, user_id);
            statement.setFloat(2, rank);

            ResultSet r = statement.executeQuery();

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

    private List<Float> getRanks(List<Long> chunkIDs) {
        try {
            List<Float> ranks = new ArrayList<>();
            for (Long id : chunkIDs) {
                PreparedStatement statement = db.con.prepareStatement("SELECT rank FROM chunk WHERE chunk_id = ?;");
                statement.setLong(1, id);
                ranks.add(statement.executeQuery().getFloat(1));
            }
            return ranks;
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private List<String> getChunkOwnerUsernames(List<Long> chunkIDs) {
        try {
            List<String> usernames = new ArrayList<>();
            for (Long chunkID : chunkIDs) {
                PreparedStatement statement = db.con.prepareStatement(GET_USERNAME_FROM_CHUNK_ID_QUERY);
                statement.setLong(1, chunkID);
                ResultSet r = statement.executeQuery();

                usernames.add(r.getString("username"));
            }
            return usernames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveChunk(Chunk chunk, float rank) {

        try {
            PreparedStatement preparedStatement = db.con.prepareStatement(SAVE_CHUNK_QUERY, new String[] {"chunk_id"});
            preparedStatement.setLong(1, 0);
            preparedStatement.setFloat(2, rank);
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

//    private void updateChunkStatistics(ChunkPerformance performance) {
//        try {
//            PreparedStatement preparedStatement =  db.con.prepareStatement(GET_CURRENT_PERFORMANCE_QUERY);
//            preparedStatement.setLong(1, performance.chunkID);
//
//            ResultSet resultSet = preparedStatement.executeQuery();
//            resultSet.next();
//            long currentGamesPlayed = resultSet.getLong("games_played");
//            currentGamesPlayed++;
//
//            preparedStatement = db.con.prepareStatement(UPDATE_WINS_QUERY);
//            preparedStatement.setLong(1, currentGamesPlayed);
//            preparedStatement.setLong(2, performance.chunkID);
//            preparedStatement.executeUpdate();
//        }
//        catch (SQLException exception) {
//            throw new RuntimeException(exception);
//        }
//
//    }



}
