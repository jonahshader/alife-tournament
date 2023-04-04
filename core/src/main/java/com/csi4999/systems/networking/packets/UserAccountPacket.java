package com.csi4999.systems.networking.packets;

import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.creature.sensors.EyeBuilder;
import com.csi4999.systems.creature.tools.*;
import com.csi4999.systems.tournament.RankUpdater;

import java.util.ArrayList;
import java.util.List;

/**
 * this packet is sent (S -> C) on a successful login
 */
public class UserAccountPacket {

    public long userID;
    public List<ToolBuilder> toolBuilders;
    public List<SensorBuilder> sensorBuilders;

    public int maxToolsLevel = 1;
    public int maxSensorsLevel = 1;
    public int maxMutationRateLevel = 1;
    public int maxCreaturesPerSecondLevel = 1;
    public int maxInitialCreaturesLevel = 1;
    public int maxFoodLevel = 1;

    public int maxTools = 4; // TODO: turn these back down for final release
    public int maxSensors = 4;
    public int maxMutationRate = 1;
    public int maxCreaturesPerSecond = 5;
    public int maxInitialCreatures = 150;
    public int maxFood = 600;



    public boolean is_admin;


    // ranking stuff
    public float rank = RankUpdater.RANK_MEAN;
    public int gamesPlayed = 0;

    public int money = 16;

    public UserAccountPacket(){} // empty constructor for Kryo

    public static UserAccountPacket createDefault(long id) {
        UserAccountPacket p = new UserAccountPacket();
        p.userID = id;
        p.toolBuilders = new ArrayList<>();
        p.sensorBuilders = new ArrayList<>();
        p.toolBuilders.add(new MouthBuilder());
        p.toolBuilders.add(new FlagellaBuilder());
        p.toolBuilders.add(new HornBuilder());
        p.toolBuilders.add(new GripperBuilder());
        p.sensorBuilders.add(new EyeBuilder());
        return p;
    }
}
