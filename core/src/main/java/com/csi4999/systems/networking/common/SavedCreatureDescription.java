package com.csi4999.systems.networking.common;

public class SavedCreatureDescription {

    public String name;
    public String description;
    public long creatureID;

    public SavedCreatureDescription() {}

    public SavedCreatureDescription(String name, String description, long creatureID) {
        this.name = name;
        this.description = description;
        this.creatureID = creatureID;
    }
}
