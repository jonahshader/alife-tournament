package com.csi4999.systems.networking.common;

public class SavedEnvironmentDescription {

    public String name;
    public String description;

    public long environmentID;

    public SavedEnvironmentDescription(){}

    public SavedEnvironmentDescription(String name, String description, long environmentID) {
        this.name = name;
        this.description = description;
        this.environmentID = environmentID;
    }
}
