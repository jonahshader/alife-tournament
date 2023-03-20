package com.csi4999.systems.networking.packets;

import com.csi4999.systems.environment.Environment;

import java.util.List;

public class TournamentPacket {

    public Environment environment;
    public List<Long> chunkIDs;
    public List<String> names;
    public long creatorChunkID;

    public TournamentPacket(){}

    public TournamentPacket(Environment environment, List<Long> chunkIDs, List<String> names, long creatorChunkID) {
        this.environment = environment;
        this.chunkIDs = chunkIDs;
        this.names = names;
        this.creatorChunkID = creatorChunkID;
    }
}
