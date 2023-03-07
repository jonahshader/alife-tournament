package com.csi4999.systems.networking.serverlisteners;

import com.csi4999.systems.networking.Database;
import com.csi4999.systems.networking.SerializedType;
import com.csi4999.systems.networking.packets.LoginFailedPacket;
import com.csi4999.systems.networking.packets.LoginPacket;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class LoginListener implements Listener {

    private Database db;
    private Kryo k;

    private final String accountQuery = "SELECT user_id, username, password FROM user;";

    public LoginListener(Database db, Kryo k) {
        this.db = db;
        this.k = k;
    }


    @Override
    public void received(Connection c, Object o) {
        if (o instanceof LoginPacket) {
            System.out.println("SERVER: received LoginPacket from " + c.toString());
            LoginPacket p = (LoginPacket) o;

            tryLogin(c, p);
        }
    }

    private void tryLogin(Connection c, LoginPacket p) {
        try {
            Statement s = db.con.createStatement();
            ResultSet r = s.executeQuery(accountQuery);
            long id = -1;

            while (r.next()) {
                if (Objects.equals(r.getString("username"), p.account.username)
                    && Objects.equals(r.getString("password"), p.account.password)) {
                    //success
                    id = r.getLong("user_id");
                    break;
                }
            }

            if (id >= 0) {
                System.out.println("found user with id " + id);
                Object o = db.retrieveSerializedObject(SerializedType.USER_ACCOUNT, id, k);
                UserAccountPacket temp = (UserAccountPacket) o;
                System.out.println("object exists see: "+  temp.userID);
                c.sendTCP(o);
            }
            else {
                System.out.println("no user found");
                c.sendTCP(new LoginFailedPacket());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
