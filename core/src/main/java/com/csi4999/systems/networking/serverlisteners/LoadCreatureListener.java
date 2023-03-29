package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.packets.RequestCreaturePacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class LoadCreatureListener implements Listener {

    private Database db;
    private Kryo k;

    public LoadCreatureListener(Database db, Kryo k) {
        this.db = db;
        this.k = k;
    }

    @Override
    public void received(Connection c, Object o) {

        if (o instanceof RequestCreaturePacket) {
            System.out.println("Got request to send Creature");
            RequestCreaturePacket p = (RequestCreaturePacket) o;

            Creature creature = (Creature) db.retrieveSerializedObject(SerializedType.CREATURE, p.creatureID, k);
            c.sendTCP(creature);
        }
    }
}
