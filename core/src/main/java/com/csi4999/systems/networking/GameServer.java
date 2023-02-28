package com.csi4999.systems.networking;

import com.csi4999.systems.networking.serverlisteners.LoginListener;
import com.csi4999.systems.networking.serverlisteners.RegisterListener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class GameServer {
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
    }

    private void setupServer(int port) {
        server = new Server();
        server.start();
        try {
            server.bind(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
