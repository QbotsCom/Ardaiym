package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.InfoMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lol on 08.06.2017.
 */
public class InfoMessageDao extends AbstractDao {
    Connection connection;
    private static final String UPDATE_ABOUT = "UPDATE ABOUT SET TEXT = ?, PHOTO = ? WHERE ID = ?";
    private static final String SELECT_FROM_ABOUT = "SELECT * FROM ABOUT WHERE ID = ?";
    public static final int ABOUT_ID = 1;
    public static final int CONTACTS = 3;
    public static final int GROUP_ID = 4;

    public InfoMessageDao(Connection connection) {
        this.connection = connection;
    }

    public InfoMessage getInfoMessage(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_FROM_ABOUT);
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseAboutMessage(rs);
        }
        return null;
    }

    public void updateAbout(InfoMessage message) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_ABOUT);
        ps.setString(1, message.getText());
        ps.setString(2, message.getPhoto());
        ps.setInt(3, message.getId());
        ps.execute();
    }

    private InfoMessage parseAboutMessage(ResultSet rs) throws SQLException {
        InfoMessage message = new InfoMessage();
        message.setId(rs.getInt("ID"));
        message.setText(rs.getString("TEXT"));
        if (!rs.getString("PHOTO").equals("null")) {
            message.setPhoto(rs.getString("PHOTO"));
        }
        return message;
    }

}
