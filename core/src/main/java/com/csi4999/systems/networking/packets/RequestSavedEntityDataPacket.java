package com.csi4999.systems.networking.packets;

public class RequestSavedEntityDataPacket {

    public long userID;

    public RequestSavedEntityDataPacket() {}

    public RequestSavedEntityDataPacket(long userID) {
        this.userID = userID;
    }
}
