package com.csi4999.systems.networking.packets;

import com.csi4999.systems.creature.Creature;

public class SaveCreaturePacket {

    public Creature creature;

    public SaveCreaturePacket(){} //empty constructor for kryo

    public SaveCreaturePacket(Creature creature) {
        this.creature = creature;
    }

}
