package com.csi4999.systems.environment;

import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.creature.sensors.EyeBuilder;
import com.csi4999.systems.creature.tools.FlagellaBuilder;
import com.csi4999.systems.creature.tools.MouthBuilder;

import java.util.ArrayList;
import java.util.List;

public class EnvProperties {
    // for environment
    public float mutationRate = 1f;


    // for food spawner
    public int initialFood = 200;
    public int minFood = 1000;
    public float foodSpawnStd = 512f;


    // for creature spawner
    public int initialCreatures = 150;
    public int minCreatures = 0;
    public float creatureSpawnStd = 512f;
    public List<SensorBuilder> sensorBuilders = new ArrayList<>();
    public List<ToolBuilder> toolBuilders = new ArrayList<>();
    public int minSensors = 1;
    public int maxSensors = 4;
    public int minTools = 3;
    public int maxTools = 6;

    public EnvProperties(){}

    public static EnvProperties makeTestDefault() {
        EnvProperties p = new EnvProperties();
        p.sensorBuilders.add(new EyeBuilder());
        p.toolBuilders.add(new FlagellaBuilder());
        p.toolBuilders.add(new MouthBuilder());
        return p;
    }

}
