package com.csi4999.systems.networking.wrappers;

import com.csi4999.systems.environment.Environment;

/**
 * a combination of a specific user a creature and an environment holding that creature
 */
public class Chunk {

    public long userID; //db key user
    public long creatureID; //db key creature
    public Environment environment;

    public Chunk() {} // empty constructor for Kryo

    public Chunk(long userID, long creatureID, Environment environment) {
        this.userID = userID;
        this.creatureID = creatureID;
        this.environment = environment;
    }
}
