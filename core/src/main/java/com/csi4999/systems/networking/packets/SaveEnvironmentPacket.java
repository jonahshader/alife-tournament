package com.csi4999.systems.networking.packets;

import com.csi4999.systems.environment.Environment;

public class SaveEnvironmentPacket {

    public Environment environment;
    public SaveEnvironmentPacket(){} //empty constructor for kryo

    public SaveEnvironmentPacket(Environment e) {this.environment = e;}
}
