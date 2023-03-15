package com.csi4999.systems.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.zip.DeflaterOutputStream;

public class Database {

    private static final String serverPath = "Server";
    private static final String databasePath = "jdbc:sqlite:" + serverPath + "/database.db";
    private static final String userPath = serverPath + "/user_table.sql";

    private static final String creaturePath = serverPath + "/creature_table.sql";
    private static final String environmentPath = serverPath + "/environment_table.sql";

    private static final String tournamentPath = serverPath + "/tournament_table.sql";
    private static final String chunkPath = serverPath + "/chunk_table.sql";

    private static final String ctBridgePath = serverPath + "/ctBridge_table.sql";



    public Connection con;

    public Database() {
        createNewDatabase();
        populate();
    }

    /**
     * Create/connect to database, exits on failure.
     */
    private void createNewDatabase() {
        try {
            con = DriverManager.getConnection(databasePath);
            DatabaseMetaData meta = con.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
            System.out.println("A new database has been created.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void populate() {
        String createUserTable;
        String createCreatureTable;
        String createEnvironmentTable;
        String createTournamentTable;
        String createChunkTable;
        String createBridgeTable;

        try {
            createUserTable = new String(Files.readAllBytes(Paths.get(userPath)));
            createCreatureTable = new String(Files.readAllBytes(Paths.get(creaturePath)));
            createEnvironmentTable = new String(Files.readAllBytes(Paths.get(environmentPath)));
            createTournamentTable = new String(Files.readAllBytes(Paths.get(tournamentPath)));
            createChunkTable = new String(Files.readAllBytes(Paths.get(chunkPath)));
            createBridgeTable = new String(Files.readAllBytes(Paths.get(ctBridgePath)));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Statement statement = con.createStatement();
            statement.execute(createUserTable);
            statement.execute(createCreatureTable);
            statement.execute(createEnvironmentTable);
            statement.execute(createTournamentTable);
            statement.execute(createChunkTable);
            statement.execute(createBridgeTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Server
    // --Creatures
    // ----38165
    // ----38166
    // --UserAccounts
    // ----1623
    // ----1624

    private String typeToName(SerializedType type) {
        switch (type) {
            case CREATURE:
                return "Creature";
            case USER_ACCOUNT:
                return "UserAccount";
            case ENVIRONMENT:
                return "Environment";
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public Object retrieveSerializedObject(SerializedType type, long id, Kryo k) {
        try {
            Input input = new Input(new FileInputStream(serverPath + "/" + typeToName(type) + "/" + id));
            Object o = k.readClassAndObject(input);
            input.close();
            return o;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void serializeObject(SerializedType type, long id, Kryo k, Object toSerialize) {
        try {
            Output output = new Output(new FileOutputStream(serverPath + "/" + typeToName(type) + "/" + id));
            k.writeClassAndObject(output, toSerialize);
            output.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
