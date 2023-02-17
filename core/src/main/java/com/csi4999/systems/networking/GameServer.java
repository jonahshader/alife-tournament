package com.csi4999.systems.networking;

import com.csi4999.systems.networking.serverlisteners.ExampleServerListener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class GameServer {
    public Server server;

    public GameServer(int port) {
        setupServer(port);
        // any packet that goes through Kryo/KryoNet must be registered. RegisterPackets.registerPackets is where we'll do that
        RegisterPackets.registerPackets(server.getKryo());

        // example listener just prints out events and handles the ExamplePacket
        // we should make different listeners for each packet class, although that isn't strictly necessary
        ExampleServerListener l = new ExampleServerListener();
        server.addListener(l);
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
