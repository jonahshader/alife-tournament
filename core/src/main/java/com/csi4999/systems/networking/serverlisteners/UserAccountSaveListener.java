package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class UserAccountSaveListener implements Listener {

    Database db;
    Kryo k;

    public UserAccountSaveListener(Database db, Kryo k) {
        this.db = db;
        this.k = k;
    }

    @Override
    public void received(Connection c, Object o) {
        if (o instanceof UserAccountPacket) {
            System.out.println("Received User Account Packet");
            UserAccountPacket p = (UserAccountPacket) o;
        }
    }
}
