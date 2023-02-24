package com.csi4999.systems.networking.packets;


import com.csi4999.systems.networking.wrappers.Account;

/**
 * sent (c -> S) to create new account
 */
public class RegisterPacket {
    public Account account;

    public RegisterPacket(){} // empty constructor for Kryo

    public RegisterPacket(Account account) {
        this.account = account;
    }
}
