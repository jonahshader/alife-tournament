package com.csi4999.systems.networking;


import com.csi4999.systems.networking.serverlisteners.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;


import java.io.IOException;

import static com.csi4999.systems.networking.GameClient.BUFFER_SIZE;


public class GameServer {

    public static final int OFFLINE_PORT = 25721;
    public Server server;
    public Database db;

    public GameServer(int port) {
        // create database
        db = new Database();
        setupServer(port);
        // any packet that goes through Kryo/KryoNet must be registered. RegisterPackets.registerPackets is where we'll do that
        RegisterPackets.registerPackets(server.getKryo());

        server.addListener(new RegisterListener(db, server.getKryo()));
        server.addListener(new LoginListener(db, server.getKryo()));
        server.addListener(new SaveEnvironmentListener(db, server.getKryo()));
        server.addListener(new SaveCreatureListener(db, server.getKryo()));
        server.addListener(new UserAccountSaveListener(db, server.getKryo()));
        server.addListener(new SavedEntityListListener(db, server.getKryo()));
        server.addListener(new LoadEnvironmentListener(db, server.getKryo()));
        server.addListener(new TournamentListener(db, server.getKryo()));
        server.addListener(new LoadCreatureListener(db, server.getKryo()));
        server.addListener(new RankingRequestListener(db, server.getKryo()));
    }

    private void setupServer(int port) {
        server = new Server(BUFFER_SIZE, BUFFER_SIZE);
        server.getKryo().setReferences(true);
        server.start();
        try {
            server.bind(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        server.stop();
    }
}
