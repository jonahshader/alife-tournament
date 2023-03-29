package com.csi4999.systems.networking.packets;

import java.util.List;

public class NewRanksPacket {
    public List<Float> ranks;
    public NewRanksPacket() {}
    public NewRanksPacket(List<Float> ranks) {
        this.ranks = ranks;
    }
}
