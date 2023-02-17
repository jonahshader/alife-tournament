package com.csi4999.systems.networking;

import com.csi4999.systems.networking.serverlisteners.ExampleServerListener;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public final class GameClient {
    // TODO: add logic for auto-reconnecting
    private static GameClient instance = null;
    private final Client client;

    private GameClient() {
        client = new Client();
        RegisterPackets.registerPackets(client.getKryo());
        client.start();

        // reusing server example
        ExampleServerListener l = new ExampleServerListener();
        client.addListener(l);
    }

    /**
     * tries connecting to a server
     * @param ip
     * @param port
     * @throws IOException - connect may fail and throw IOException
     */
    public void tryConnect(String ip, int port) throws IOException {
        client.connect(2000, ip, port);
    }

    public static GameClient getInstance() {
        if (instance == null)
            instance = new GameClient();
        return instance;
    }
}
