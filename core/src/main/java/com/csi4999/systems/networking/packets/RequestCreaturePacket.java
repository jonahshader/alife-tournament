package com.csi4999.systems.networking.packets;

public class RequestCreaturePacket {

    public long creatureID;

    public RequestCreaturePacket(){}

    public RequestCreaturePacket(long creatureID) {
        this.creatureID = creatureID;
    }
}
