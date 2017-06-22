package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.entity.User;
import org.telegram.telegrambots.api.objects.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by user on 12/18/16.
 */
public class UserDao {
    private static final String SELECT_USER_CHAT_ID = "SELECT * FROM PUBLIC.USER WHERE ID=?";
    private static final String SELECT_FROM_USER = "SELECT * FROM USER";
    private static final String SELECT_FROM_USER_ADDED_BY = "SELECT * FROM USER WHERE ADDED_BY = ?";
    private static final String SELECT_FROM_USER_BY_CHAT_ID = "SELECT * FROM USER WHERE CHAT_ID = ?";
    private static final String ADD_USER = "INSERT INTO USER (ID, CHAT_ID, NAME, ADDED_BY) VALUES (default, ?, ?, ?)";
    private static final String DELETE_USER = "DELETE FROM USER WHERE CHAT_ID = ?";
    private static final int PARAMETER_USER_ID = 1;
    private static final int PARAMETER_CHAT_ID = 1;
    private static final int CHAT_ID_COLUMN_INDEX = 2;
    private static final int USER_ID_COLUMN_INDEX = 1;
    public static final int ADMIN_ID = 1;
    private Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public Long getAdminChatId() {
        try {
            PreparedStatement ps = connection.prepareStatement(SELECT_USER_CHAT_ID);
            ps.setLong(PARAMETER_USER_ID, ADMIN_ID);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            return rs.getLong(CHAT_ID_COLUMN_INDEX);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAdmin(Long chatId) throws SQLException {
        return getUserByChatId(chatId).getRules() == 2;
    }

    public Long getChatIdByUserId(Long id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_USER_CHAT_ID);
        ps.setLong(PARAMETER_USER_ID, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        return rs.getLong(CHAT_ID_COLUMN_INDEX);
    }

    public Long getUserIdByChatId(Long chatId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_FROM_USER_BY_CHAT_ID);
        ps.setLong(PARAMETER_CHAT_ID, chatId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        return rs.getLong(USER_ID_COLUMN_INDEX);
    }

    public User getUserByChatId(Long chatId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_FROM_USER_BY_CHAT_ID);
        ps.setLong(1, chatId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return parseUser(rs);
        }
        return null;
    }

    User parseUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("ID"));
        user.setChatId(rs.getLong("CHAT_ID"));
        user.setName(rs.getString("NAME"));
        user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
        user.setHaveCar(rs.getBoolean("HAVE_CAR"));
        user.setCity(rs.getString("CITY"));
        user.setSex(rs.getBoolean("SEX"));
        user.setBirthday(rs.getString("BIRTHDAY"));
        user.setAddedBy(rs.getLong("ADDED_BY"));
        user.setAdded(rs.getBoolean("IS_ADDED"));
        user.setRules(rs.getInt("RULES"));
        return user;
    }

    public boolean addUser(Contact contact, Long addedBy) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(ADD_USER);
        ps.setInt(1, contact.getUserID());
        ps.setString(2, contact.getFirstName());
        ps.setLong(3, addedBy);
        ps.execute();
//        updateUsers();
        return true;
    }

    public void addUser(User user) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("INSERT INTO USER (CHAT_ID, NAME, PHONE_NUMBER, HAVE_CAR, CITY, SEX, BIRTHDAY) VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps.setLong(1, user.getChatId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPhoneNumber());
        ps.setBoolean(4, user.isHaveCar());
        ps.setString(5, user.getCity());
        ps.setBoolean(6, user.isSex());
        ps.setString(7, user.getBirthday());
        ps.execute();
//        ResultSet rs = ps.getResultSet();
//        rs.next();
//        return parseUser(rs);
    }

    public void deleteUser(int userId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(DELETE_USER);
        ps.setInt(1, userId);
        ps.execute();
//        updateUsers();
    }

    public void updateUser(User user) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE USER SET ADDED_BY = ?, IS_ADDED = ?, RULES = ? WHERE ID = ?");
        ps.setLong(1, user.getAddedBy());
        ps.setBoolean(2, user.isAdded());
        ps.setInt(3, user.getRules());
        ps.setInt(4, user.getId());
        ps.execute();
    }

    public User getUserByName(String name) throws SQLException {

        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER WHERE NAME = ?");
        ps.setString(1, name);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            return parseUser(rs);
        }
        return null;

    }

    public User getUserByUserId(Long userId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER WHERE ID = ?");
        ps.setLong(1, userId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        return parseUser(rs);
    }

    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER WHERE IS_ADDED = TRUE");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            users.add(parseUser(rs));
        }
        return users;
    }

    public List<User> getUsers(int rules) throws SQLException {
        List<User> users = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER WHERE RULES = ?");
        ps.setInt(1, rules);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            users.add(parseUser(rs));
        }
        return users;
    }
}
