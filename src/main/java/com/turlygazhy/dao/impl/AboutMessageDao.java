package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.AboutMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lol on 08.06.2017.
 */
public class AboutMessageDao extends AbstractDao {
    Connection connection;
    private static final int ABOUT_ID = 1;
    private static final int PLANS_FOR_YEAR = 2;
    private static final int CONTACTS = 3;
    private static final int GROUP_ID = 4;
    private static final String SELECT_FROM_ABOUT = "SELECT * FROM ABOUT WHERE ID = ?";

    public AboutMessageDao(Connection connection) {
        this.connection = connection;
    }

    public AboutMessage getAbout() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_FROM_ABOUT);
        ps.setInt(1, ABOUT_ID);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseAboutMessage(rs);
        }
        return null;

    }

    public AboutMessage getPlansForYear() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_FROM_ABOUT);
        ps.setInt(1, PLANS_FOR_YEAR);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseAboutMessage(rs);
        }
        return null;
    }

    public AboutMessage getContacts() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_FROM_ABOUT);
        ps.setInt(1, CONTACTS);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseAboutMessage(rs);
        }
        return null;
    }

    private AboutMessage parseAboutMessage(ResultSet rs) throws SQLException {
        AboutMessage message = new AboutMessage();
        message.setId(rs.getInt("ID"));
        message.setText(rs.getString("TEXT"));
        message.setPhoto(rs.getString("PHOTO"));
        return message;
    }

    public AboutMessage getGroup() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_FROM_ABOUT);
        ps.setInt(1, GROUP_ID);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseAboutMessage(rs);
        }
        return null;
    }
}
