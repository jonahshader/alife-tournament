package com.csi4999.systems.networking.packets;

import com.csi4999.systems.networking.wrappers.Chunk;

/**
 * sent (C->S) to request a new tournament's creation
 */
public class RequestTournamentPacket {
    public Chunk chunk;

    // this comes from the user's rank, and is used to initialize the chunk's rank and make a tournament with a similar rank
    public float rank;

    public  RequestTournamentPacket(){} // empty constructor for Kryo

    public  RequestTournamentPacket(Chunk chunk, float rank) {

        this.chunk = chunk;
        this.rank = rank;
    }
}
