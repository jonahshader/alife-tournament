package com.csi4999.systems.networking.common;

public class ChunkPerformance {

    public long chunkID;

    public float proportionOfWin;

    public ChunkPerformance(){}

    public ChunkPerformance(long chunkID, float proportionOfWin) {
        this.chunkID = chunkID;
        this.proportionOfWin = proportionOfWin;
    }
}
