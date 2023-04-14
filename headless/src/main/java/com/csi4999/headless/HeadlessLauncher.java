package com.csi4999.headless;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.csi4999.ALifeApp;
import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.GameServer;
import com.csi4999.systems.networking.packets.ExamplePacket;

import java.util.Scanner;

/** Launches the headless application. Can be converted into a utilities project or a server application. */
public class HeadlessLauncher {
    public static void main(String[] args) {
//        createApplication();
        int port = args.length == 1 ? Integer.parseInt(args[0]) : 25565;
        System.out.println("Starting server on port " + port + "...");
        GameServer server = new GameServer(port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping server...");
            server.shutdown();
        }));

    }

    private static Application createApplication() {
        // Note: you can use a custom ApplicationListener implementation for the headless project instead of ALifeApp.
        return new HeadlessApplication(new ALifeApp(), getDefaultConfiguration());
    }

    private static HeadlessApplicationConfiguration getDefaultConfiguration() {
        HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
        configuration.updatesPerSecond = -1; // When this value is negative, ALifeApp#render() is never called.
        //// If the above line doesn't compile, it is probably because the project libGDX version is older.
        //// In that case, uncomment and use the below line.
        //configuration.renderInterval = -1f; // When this value is negative, ALifeApp#render() is never called.
        return configuration;
    }
}
