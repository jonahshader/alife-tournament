package com.csi4999.systems.networking;

import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.networking.common.Account;
import com.csi4999.systems.networking.packets.*;
import com.esotericsoftware.kryo.Kryo;
import java.util.List;
import java.util.ArrayList;

public class RegisterPackets {
    public static void registerPackets(Kryo k) {
        k.register(String.class);
        k.register(ExamplePacket.class);
        k.register(RegisterPacket.class);
        k.register(Account.class);
        k.register(RegisterSuccessPacket.class);
        k.register(RegisterFailPacket.class);
        k.register(UserAccountPacket.class);
        k.register(List.class);
        k.register(ArrayList.class);
        k.register(LoginPacket.class);
        k.register(LoginFailedPacket.class);
        k.register(ToolBuilder.class);
        k.register(SensorBuilder.class);
    }
}
