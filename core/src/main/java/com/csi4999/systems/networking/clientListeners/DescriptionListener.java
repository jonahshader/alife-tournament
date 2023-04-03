package com.csi4999.systems.networking.clientListeners;

import com.csi4999.screens.SavedEntitiesScreen;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.networking.common.SavedCreatureDescription;
import com.csi4999.systems.networking.common.SavedEnvironmentDescription;
import com.csi4999.systems.networking.packets.SavedEntityDataPacket;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.util.List;

public class DescriptionListener implements Listener {

    private static DescriptionListener instance;
    public volatile boolean ready;

    public List<SavedCreatureDescription> creatureDescriptions;
    public List<SavedEnvironmentDescription> environmentDescriptions;






    public static DescriptionListener getInstance() {
        if (instance == null)
            instance = new DescriptionListener();
        return instance;
    }
    @Override
    public void received(Connection c, Object o) {
        if (o instanceof SavedEntityDataPacket) {
            System.out.println("Got saved entity data packet");
            SavedEntityDataPacket p = (SavedEntityDataPacket) o;

            ready = false;

            this.creatureDescriptions = p.creatureDescriptions;
            this.environmentDescriptions = p.environmentDescriptions;

            ready = true;


        }
    }
}
