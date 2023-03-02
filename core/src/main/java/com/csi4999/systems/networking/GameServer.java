package com.csi4999.systems.networking;

import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.serverlisteners.LoginListener;
import com.csi4999.systems.networking.serverlisteners.RegisterListener;
import com.csi4999.systems.networking.serverlisteners.SaveEnvironmentListener;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

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
        server.addListener(new SaveEnvironmentListener(db, server.getKryo()));

    }

    private void setupServer(int port) {
        server = new Server(3000000, 3000000);
        server.getKryo().setReferences(true);
        server.start();
        try {
            server.bind(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
