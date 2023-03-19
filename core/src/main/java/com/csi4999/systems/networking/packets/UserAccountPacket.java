package com.csi4999.systems.networking.packets;

import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.creature.sensors.EyeBuilder;
import com.csi4999.systems.creature.tools.FlagellaBuilder;
import com.csi4999.systems.creature.tools.MouthBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * this packet is sent (S -> C) on a successful login
 */
public class UserAccountPacket {
    private static final long NUMBER_OF_TOOLS_DEFAULT = 3;
    private static final long NUMBER_OF_SENSORS_DEFAULT = 2;

    public long userID;
    public List<ToolBuilder> toolBuilders;
    public List<SensorBuilder> sensorBuilders;

    public long numberOfTools;
    public long numberOfSensors;

    public float wins;
    public float confidence;

    public UserAccountPacket(){} // empty constructor for Kryo

    public UserAccountPacket(long userID, List<ToolBuilder> toolBuilders, List<SensorBuilder> sensorBuilders,
                             long numberOfTools, long numberOfSensors) {
        this.userID = userID;
        this.toolBuilders = toolBuilders;
        this.sensorBuilders = sensorBuilders;
        // are these just the length of the list ?
        this.numberOfTools = numberOfTools;
        this.numberOfSensors = numberOfSensors;
        this.wins = 0;
        this.confidence = 0;
    }

    public static UserAccountPacket createDefault(long id) {
        UserAccountPacket p = new UserAccountPacket();
        p.userID = id;
        p.toolBuilders = new ArrayList<>();
        p.sensorBuilders = new ArrayList<>();
        p.toolBuilders.add(new MouthBuilder());
        p.toolBuilders.add(new FlagellaBuilder());
        p.sensorBuilders.add(new EyeBuilder());
        p.numberOfTools = NUMBER_OF_TOOLS_DEFAULT;
        p.numberOfSensors = NUMBER_OF_SENSORS_DEFAULT;
        return p;
    }
}
