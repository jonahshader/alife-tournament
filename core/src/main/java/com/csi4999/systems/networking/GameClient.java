package com.csi4999.systems.networking;

import com.csi4999.systems.networking.clientListeners.RegisterFeedbackListener;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public final class GameClient {
    // TODO: add logic for auto-reconnecting
    private static GameClient instance = null;
    public final Client client;

    public static final int BUFFER_SIZE = 80_000_000;

    public volatile UserAccountPacket user;

    private GameClient() {
        client = new Client(BUFFER_SIZE, BUFFER_SIZE);
        client.getKryo().setReferences(true);
        RegisterPackets.registerPackets(client.getKryo());

        client.start();

        client.addListener(RegisterFeedbackListener.getInstance());
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
