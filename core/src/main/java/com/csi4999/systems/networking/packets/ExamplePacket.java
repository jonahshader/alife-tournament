package com.csi4999.systems.networking.packets;

public class ExamplePacket {
    public String message;
    public ExamplePacket(){} // needs empty constructor to be serialized by Kryo
    public ExamplePacket(String message) {
        this.message = message;
    }
}
