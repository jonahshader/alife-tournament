package com.csi4999.systems.networking.packets;

import com.csi4999.systems.networking.wrappers.Chunk;

/**
 * sent (C->S) to request a new tournament's creation
 */
public class RequestTournamentPacket {
    public Chunk chunk;
    public long difficulty;

    public  RequestTournamentPacket(){} // empty constructor for Kryo

    public  RequestTournamentPacket(Chunk chunk, long difficulty) {

        this.chunk = chunk;
        this.difficulty = difficulty;
    }
}
