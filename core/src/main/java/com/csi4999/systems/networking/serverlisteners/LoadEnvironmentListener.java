package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.packets.RequestEnvironmentPacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoadEnvironmentListener implements Listener {

    private Database db;
    private Kryo k;

    public LoadEnvironmentListener(Database db, Kryo k) {
        this.db = db;
        this.k = k;
    }


    @Override
    public void received(Connection c, Object o) {
        if (o instanceof RequestEnvironmentPacket){
            System.out.println("Got Request to send Env");
            RequestEnvironmentPacket p = (RequestEnvironmentPacket) o;

            Environment e = (Environment) db.retrieveSerializedObject(SerializedType.ENVIRONMENT, p.envID, k);
            c.sendTCP(e);

        }
    }
}
