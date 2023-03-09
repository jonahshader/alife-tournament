package com.csi4999.systems.networking.clientListeners;

import com.csi4999.systems.environment.Environment;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class LoadListener implements Listener {

    private static LoadListener instance;
    public Environment environment;
    public volatile boolean ready;

    private LoadListener(){this.ready=false;}
    public static LoadListener getInstance() {
        if (instance == null) {
            instance = new LoadListener();
        }
        return instance;
    }

    @Override
    public void received(Connection c, Object o) {
        if (o instanceof Environment) {
            System.out.println("Received Env");

            ready = false;
            this.environment = (Environment) o;
            ready = true;
        }
    }
}
