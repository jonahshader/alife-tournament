package com.csi4999.systems.networking.packets;

public class RequestEnvironmentPacket {

    public long envID;
    public RequestEnvironmentPacket(){}
    public RequestEnvironmentPacket(long evdID) {
        this.envID = evdID;
    }
}
