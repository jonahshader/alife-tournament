package com.csi4999.systems.networking.wrappers;

import com.csi4999.systems.environment.Environment;

/**
 * a combination of a specific user a creature and an environment holding that creature
 */
public class Chunk {

    public long chunkID;
    public Environment environment;

    public Chunk() {} // empty constructor for Kryo

    public Chunk(Environment environment) {
        this.environment = environment;
    }
}
