package com.csi4999.systems.networking.packets;

import com.csi4999.systems.networking.common.ChunkPerformance;

import java.util.List;

public class TournamentResultsPacket {


    public List<ChunkPerformance> performances;

    public TournamentResultsPacket(){}

    public TournamentResultsPacket(List<ChunkPerformance> performances){this.performances = performances;}
}
