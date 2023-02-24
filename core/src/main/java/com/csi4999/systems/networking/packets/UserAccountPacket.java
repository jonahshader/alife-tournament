package com.csi4999.systems.networking.packets;

import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import java.util.List;

/**
 * this packet is sent (S -> C) on a successful login
 */
public class UserAccountPacket {
    public long userID;
    public List<ToolBuilder> toolBuilders;
    public List<SensorBuilder> sensorBuilders;

    public long numberOfTools;
    public long numberOfSensors;

    public  UserAccountPacket(){} // empty constructor for Kryo

    public  UserAccountPacket(long userID, List<ToolBuilder> toolBuilders, List<SensorBuilder> sensorBuilders, long numberOfTools, long numberOfSensors) {
        this.userID = userID;
        this.toolBuilders = toolBuilders;
        this.sensorBuilders = sensorBuilders;

        // are these just the length of the list ?
        this.numberOfTools = numberOfTools;
        this.numberOfSensors = numberOfSensors;
    }

}
