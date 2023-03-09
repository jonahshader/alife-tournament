package com.csi4999.systems.networking;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.csi4999.systems.ai.SparseBrain;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.creature.sensors.Eye;
import com.csi4999.systems.creature.sensors.EyeBuilder;
import com.csi4999.systems.creature.tools.*;
import com.csi4999.systems.environment.CreatureSpawner;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.environment.Food;
import com.csi4999.systems.environment.FoodSpawner;
import com.csi4999.systems.networking.common.Account;
import com.csi4999.systems.networking.common.SavedCreatureDescription;
import com.csi4999.systems.networking.common.SavedEnvironmentDescription;
import com.csi4999.systems.networking.packets.*;
import com.csi4999.systems.physics.PhysicsEngine;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

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

        // classes for environment save
//        k.register(Random.class);
        k.register(PhysicsEngine.class);
        k.register(FoodSpawner.class);
        k.register(CreatureSpawner.class);
        k.register(SaveEnvironmentPacket.class);
        k.register(Environment.class);
        k.register(MouthBuilder.class);
        k.register(Creature.class);
        k.register(Vector2.class);
        k.register(Rectangle.class);
        k.register(SparseBrain.class);
        k.register(float[].class);
        k.register(int[].class);
        k.register(Eye.class);
        k.register(Color.class);
        k.register(Matrix4.class);
        k.register(Affine2.class);
        k.register(Vector3.class);
        k.register(Flagella.class);
        k.register(Mouth.class);
        k.register(MouthPart.class);
        k.register(Food.class);
        k.register(ReentrantLock.class, new JavaSerializer());
        k.register(RandomXS128.class, new JavaSerializer());
        k.register(EyeBuilder.class);
        k.register(FlagellaBuilder.class);

        // classes for creature save
        k.register(SaveCreaturePacket.class);

        // classes for env load
        k.register(RequestSavedEntityDataPacket.class);
        k.register(SavedEnvironmentDescription.class);
        k.register(SavedEntityDataPacket.class);
        k.register(SavedCreatureDescription.class);
        k.register(RequestEnvironmentPacket.class);

    }
}
