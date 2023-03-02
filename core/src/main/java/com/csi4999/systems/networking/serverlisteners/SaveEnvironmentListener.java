package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.packets.SaveEnvironmentPacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SaveEnvironmentListener implements Listener {

    private Database db;

    private Kryo k;

    private final String saveEnvironmentQuery = "INSERT INTO environment (user_id) VALUES(?);";
    private final String EnvironmentListQuery = "SELECT * FROM environment;";

    public SaveEnvironmentListener(Database db, Kryo k) {
        this.db = db;
        this.k = k;
    }

    @Override
    public void received(Connection c, Object o) {
        if (o instanceof SaveEnvironmentPacket) {
            System.out.println("Received Save Environment packet");
            SaveEnvironmentPacket p = (SaveEnvironmentPacket) o;
            trySave(c, p);
        }
    }

    private void trySave(Connection c, SaveEnvironmentPacket p) {

        if (p.environment.EnvironmentID == 0) {
            /*
             if the environmentID has never been set we should set it here
             this would be the case if the environment is newly created
            */
            saveWithNoID(p.environment);

        }
        else {
           /*
            if the environment already has a set id then we first check the database to make sure
            we have a record of such an environment. If we do, we don't need to do anything db-wise, just need to update the file
            otherwise we need to reset its PK to a correct value and insert
           */
            saveWithID(p.environment);
        }

    }

    private void saveWithNoID(Environment e){
        try (PreparedStatement s = db.con.prepareStatement(saveEnvironmentQuery, new String[] {"environment_id"})) {
            s.setLong(1, e.userID);
            s.executeUpdate();

            ResultSet keys = s.getGeneratedKeys();
            System.out.println(keys);
            keys.next();
            e.EnvironmentID = keys.getLong(1);
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        db.serializeObject(SerializedType.ENVIRONMENT, e.EnvironmentID, k, e);
    }

    private void saveWithID(Environment e) {
        boolean success = false;
        try {
            Statement stmt = db.con.createStatement();
            ResultSet r = stmt.executeQuery(EnvironmentListQuery);

            while (r.next()) {
                if (r.getLong("environment_id") == e.EnvironmentID && r.getLong("user_id") == e.userID) {
                    success = true;
                    break;
                }
            }

            if (success) {
                System.out.println("found matching environment! updating file...");

                db.serializeObject(SerializedType.ENVIRONMENT, e.EnvironmentID, k, e);
            }
            else {
                System.out.println("Received Environment contained bogus id\nmaking new entry in table for it");
                saveWithNoID(e);
            }
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

    }
}
