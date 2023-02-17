package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.networking.packets.ExamplePacket;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ExampleServerListener implements Listener {
    // NOTE: Listener has default implementations so you only need to implement the functions you need
    // (often thats just gonna be the receive method)
    @Override
    public void connected(Connection connection) {
        System.out.println("new connection: " + connection.toString());
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("disconnected: " + connection.toString());
    }

    @Override
    public void received(Connection connection, Object o) {
        System.out.println("received object from " + connection.toString());
        if (o instanceof ExamplePacket) {
            // need to downcast to ExamplePacket from Object
            ExamplePacket p = (ExamplePacket) o;
            System.out.println("got ExamplePacket with message: " + p.message);
        }
    }

    @Override
    public void idle(Connection connection) {
        // no idea what this does
//        System.out.println("idle...?: " + connection.toString());
    }
}
