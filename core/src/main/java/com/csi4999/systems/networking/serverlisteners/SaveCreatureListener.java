package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.packets.SaveCreaturePacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles saving a creature on creation or update. Does not handle updating a creature's win-loss
 * See tournament result packet (todo)
 */
public class SaveCreatureListener implements Listener {

    private Kryo k;
    private Database db;

    private final String saveCreatureQuery = "INSERT INTO creature (wins,losses,user_id) VALUES(?,?,?);";
    private final String creatureListQuery = "SELECT * FROM creature;";

    public SaveCreatureListener(Database db, Kryo k) {
        this.db = db;
        this.k = k;
    }

    @Override
    public void received(Connection c, Object o) {

        if (o instanceof SaveCreaturePacket) {

            System.out.println("Received Save Creature packet");
            SaveCreaturePacket p = (SaveCreaturePacket) o;
            trySave(c,p);
        }

    }

    private void trySave(Connection c, SaveCreaturePacket p) {

        if (p.creature.creatureID == 0) {
            //the creature is new and must be assigned an id
            saveNewCreature(p.creature);
        }
        else {
            //the creature is being updated
            updateExistingCreature(p.creature);
        }

    }

    private void saveNewCreature(Creature c) {
        try (PreparedStatement s = db.con.prepareStatement(saveCreatureQuery, new String[] {"creature_id"})) {
            s.setInt(1,0);
            s.setInt(2,0);
            s.setLong(3, c.userID);
            s.executeUpdate();

            ResultSet keys = s.getGeneratedKeys();
            keys.next();
            c.creatureID = keys.getLong(1);
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        db.serializeObject(SerializedType.CREATURE, c.creatureID, k, c);
    }

    private void updateExistingCreature(Creature c) {
        boolean success = false;
        try {
            Statement stmt = db.con.createStatement();
            ResultSet r = stmt.executeQuery(creatureListQuery);

            while (r.next()) {
                if (r.getLong("creature_id") == c.creatureID && r.getLong("user_id") == c.userID) {
                    success = true;
                    break;
                }
            }

            if (success) {
                System.out.println("found matching creature! updating file...");

                db.serializeObject(SerializedType.CREATURE, c.creatureID, k, c);
            }
            else {
                System.out.println("Received Environment contained bogus id\nmaking new entry in table for it");
                saveNewCreature(c);
            }
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
