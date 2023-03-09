package com.csi4999.systems.networking.packets;

import com.csi4999.systems.networking.common.SavedCreatureDescription;
import com.csi4999.systems.networking.common.SavedEnvironmentDescription;

import java.util.List;

public class SavedEntityDataPacket {
    public List<SavedEnvironmentDescription> environmentDescriptions;
    public List<SavedCreatureDescription> creatureDescriptions;

    public SavedEntityDataPacket() {}

    public SavedEntityDataPacket(List<SavedEnvironmentDescription> environmentDescriptions, List<SavedCreatureDescription> creatureDescriptions) {
        this.environmentDescriptions = environmentDescriptions;
        this.creatureDescriptions = creatureDescriptions;
    }
}
