package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.packets.RegisterFailPacket;
import com.csi4999.systems.networking.packets.RegisterPacket;
import com.csi4999.systems.networking.packets.RegisterSuccessPacket;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class RegisterListener implements Listener {

    private Database db;
    private Kryo k;

    private final String usernameQuery = "SELECT username FROM user;";
    private final String createUserQuery = "INSERT INTO user(username, password) VALUES(?,?)";

    private final String getUserIDQuery = "SELECT user_id FROM user WHERE username = ? AND password = ?";

    public RegisterListener(Database db, Kryo k) {
        this.db = db;
        this.k = k;
    }

    @Override
    public void received(Connection c, Object o) {
        if (o instanceof RegisterPacket) {
            System.out.println("received registerPacket from " + c.toString());
            RegisterPacket p = (RegisterPacket) o;

            tryRegisterAccount(c, p);
        }
    }

    /**
     * tryRegisterAccount will try to register account p
     * if the username exists already, it will fail and send RegisterFailPacket to client
     * if the username does not exist, it succeeds and send new UserAccountPacket to client and serialize it
     * @param p the account to create
     */
    private void tryRegisterAccount(Connection c, RegisterPacket p) {
        try {
            Statement s = db.con.createStatement();
            ResultSet r = s.executeQuery(usernameQuery);
            boolean success = true;

            while (r.next()) {
                if (Objects.equals(r.getString("username"), p.account.username)) {
                    //fail
                    success = false;
                    break;
                }
            }

            if (success) {
                PreparedStatement createAccountStatement = db.con.prepareStatement(createUserQuery);
                createAccountStatement.setString(1, p.account.username);
                createAccountStatement.setString(2, p.account.password);
                createAccountStatement.executeUpdate();

                PreparedStatement getIDStatement = db.con.prepareStatement(getUserIDQuery);
                getIDStatement.setString(1, p.account.username);
                getIDStatement.setString(2, p.account.password);

                ResultSet result = getIDStatement.executeQuery();
                result.next();
                long userID = result.getLong("user_id");
                System.out.println("found user with id " + userID); //log
                UserAccountPacket newUser = UserAccountPacket.createDefault(userID);
                db.serializeObject(SerializedType.USER_ACCOUNT, newUser.userID, k, newUser);
                c.sendTCP(new RegisterSuccessPacket());
            } else {
                c.sendTCP(new RegisterFailPacket());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
