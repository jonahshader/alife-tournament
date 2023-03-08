package com.csi4999.systems.networking.packets;

import com.csi4999.systems.networking.common.Account;

/**
 * sent (C->S) to login
 */
public class LoginPacket {
    public Account account;

    public  LoginPacket(){} // empty constructor for Kryo

    public LoginPacket(Account account) {this.account = account;}
}
