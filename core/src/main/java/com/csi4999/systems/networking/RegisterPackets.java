package com.csi4999.systems.networking;

import com.csi4999.systems.networking.packets.ExamplePacket;
import com.esotericsoftware.kryo.Kryo;

public class RegisterPackets {
    public static void registerPackets(Kryo k) {
        k.register(String.class);
        k.register(ExamplePacket.class);
    }
}
