package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.common.SavedCreatureDescription;
import com.csi4999.systems.networking.common.SavedEnvironmentDescription;
import com.csi4999.systems.networking.packets.SavedEntityDataPacket;
import com.csi4999.systems.networking.packets.RequestSavedEntityDataPacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SavedEntityListListener implements Listener {

    private Kryo k;
    private Database db;

    private final String ENV_LIST_QUERY = "SELECT environment_id FROM environment WHERE user_id = ?;";
    private final String CREATURE_LIST_QUERY = "SELECT creature_id FROM creature WHERE user_id = ?;";

    public SavedEntityListListener( Database db, Kryo k) {
        this.k = k;
        this.db = db;
    }

    @Override
    public void received(Connection c, Object o) {

        if (o instanceof RequestSavedEntityDataPacket) {
            System.out.println("Got Request for saved entities");
            RequestSavedEntityDataPacket p = (RequestSavedEntityDataPacket) o;

            sendList(c, p);

        }
    }

    private void sendList(Connection c, RequestSavedEntityDataPacket p) {
        List<SavedEnvironmentDescription> envs = new ArrayList<>();

        try {
            PreparedStatement s = db.con.prepareStatement(ENV_LIST_QUERY);
            s.setLong(1, p.userID);
            ResultSet result = s.executeQuery();

            while (result.next()) {
                long env_id = result.getLong("environment_id");
                Environment e = (Environment) db.retrieveSerializedObject(SerializedType.ENVIRONMENT, env_id, k);
                System.out.println(e.environmentName);
                envs.add(new SavedEnvironmentDescription(e.environmentName, e.EnvironmentDescription, env_id));
            }

        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        List<SavedCreatureDescription> creatures = new ArrayList<>();

        try {
            PreparedStatement s = db.con.prepareStatement(CREATURE_LIST_QUERY);
            s.setLong(1, p.userID);
            ResultSet result = s.executeQuery();

            while (result.next()) {
                long creature_id = result.getLong("creature_id");
                Creature creature = (Creature) db.retrieveSerializedObject(SerializedType.CREATURE, creature_id, k);
                System.out.println(creature.creatureName);
                creatures.add(new SavedCreatureDescription(creature.creatureName, creature.creatureDescription, creature_id));
            }

        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        c.sendTCP(new SavedEntityDataPacket(envs, creatures));

    }
}
