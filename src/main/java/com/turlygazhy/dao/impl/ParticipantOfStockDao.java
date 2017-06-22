package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.entity.ParticipantOfStock;
import com.turlygazhy.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lol on 05.06.2017.
 */
public class ParticipantOfStockDao extends AbstractDao{
    Connection connection;

    public ParticipantOfStockDao(Connection connection) {
        this.connection = connection;
    }

    public List<ParticipantOfStock> getParticipantOfStock(int stockId) throws SQLException {
        List<ParticipantOfStock> participantOfStocks = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PARTICIPANTS_OF_STOCK WHERE STOCK_ID = ?");
        ps.setInt(1, stockId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            participantOfStocks.add(parseParticipantOfStock(rs));
        }
        return participantOfStocks;
    }

    public ParticipantOfStock getParticipantOfStockById(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PARTICIPANTS_OF_STOCK WHERE ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            return parseParticipantOfStock(rs);
        }
        return null;
    }

    private ParticipantOfStock parseParticipantOfStock(ResultSet rs) throws SQLException {
        ParticipantOfStock participantOfStock = new ParticipantOfStock();
        participantOfStock.setId(rs.getInt("ID"));
        participantOfStock.setUser(DaoFactory.getFactory().getUserDao().getUserByChatId(rs.getLong("USER_ID")));
        participantOfStock.setTypeOfWork(rs.getString("TYPE_OF_WORK"));
        participantOfStock.setReport(rs.getString("REPORT"));
        participantOfStock.setFinished(rs.getBoolean("FINISHED"));
        participantOfStock.setStockId(rs.getInt("STOCK_ID"));
        return participantOfStock;
    }

    public void addParticipantToStock(int stockId, Long userId, String typeOfWork) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PARTICIPANTS_OF_STOCK (STOCK_ID, USER_ID, TYPE_OF_WORK) VALUES(?, ?, ?)");
        ps.setInt(1, stockId);
        ps.setLong(2, userId);
        ps.setString(3, typeOfWork);
        ps.execute();
    }

    public void updateParticipantOFStock(ParticipantOfStock participantOfStock) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PARTICIPANTS_OF_STOCK SET REPORT = ?, FINISHED = ? WHERE ID = ?");
        ps.setString(1, participantOfStock.getReport());
        ps.setBoolean(2, participantOfStock.isFinished());
        ps.setInt(3, participantOfStock.getId());
        ps.execute();
    }

    public List<ParticipantOfStock> getParticipantOfStock(Long chatId, boolean finished) throws SQLException {
        List<ParticipantOfStock> participantOfStocks = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PARTICIPANTS_OF_STOCK WHERE USER_ID = ? and FINISHED = ?");
        ps.setLong(1, chatId);
        ps.setBoolean(2, finished);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            participantOfStocks.add(parseParticipantOfStock(rs));
        }
        return participantOfStocks;
    }
}
