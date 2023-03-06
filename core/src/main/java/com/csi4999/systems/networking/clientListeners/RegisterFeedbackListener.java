package com.csi4999.systems.networking.clientListeners;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.csi4999.ALifeApp;
import com.csi4999.screens.LoginScreen;
import com.csi4999.screens.SimScreen;
import com.csi4999.systems.networking.packets.LoginFailedPacket;
import com.csi4999.systems.networking.packets.RegisterFailPacket;
import com.csi4999.systems.networking.packets.RegisterSuccessPacket;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class RegisterFeedbackListener implements Listener {
    private static RegisterFeedbackListener instance;
    public Label accountResponse;
    public LoginScreen loginScreen;

    public static RegisterFeedbackListener getInstance() {
        if (instance == null)
            instance = new RegisterFeedbackListener();
        return instance;
    }

    @Override
    public void received(Connection c, Object o) {
        if (accountResponse != null && loginScreen != null) {
            if (o instanceof RegisterFailPacket) {
                System.out.println("got Reg Fail Packet");
                accountResponse.setText("Failed to register account, username already exists!");
                accountResponse.setColor(1f, 0.8f, 0.8f, 1f);
            } else if (o instanceof RegisterSuccessPacket) {
                System.out.println("got Reg Success Packet");
                accountResponse.setText("Successfully registered account!\nNow you may login.");
                accountResponse.setColor(0.8f, 1f, 0.8f, 1f);
            } else if (o instanceof UserAccountPacket) {
                System.out.println("got user account packet");
                loginScreen.user = (UserAccountPacket) o;
                loginScreen.loginDoneQueue = true;
            } else if (o instanceof LoginFailedPacket) {
                System.out.println("got Login Fail Packet");
                accountResponse.setText("Login Failed\nIncorrect Username or Password");
                accountResponse.setColor(1f, 0.8f, 0.8f, 1f);
            }

        }

    }
}
