package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.entity.Stock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lol on 05.06.2017.
 */
public class StockDao extends AbstractDao {
    private Connection connection;

    public StockDao(Connection connection) {
        this.connection = connection;
    }

    public Stock getStock(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM STOCK WHERE ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseStock(rs);
        }
        return null;
    }

    public void updateStock(Stock stock) throws SQLException{

    }

    public List<Stock> getAllStocks() throws SQLException{
        List<Stock> stocks = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM STOCK");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            stocks.add(parseStock(rs));
        }
        return stocks;
    }

    public Stock addStock(Stock stock) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("INSERT INTO STOCK VALUES(default, ?, ?, ?, ?, null, default)", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, stock.getName());
        ps.setString(2, stock.getDescription());
        ps.setString(3, stock.getDate());
        StringBuilder sb = new StringBuilder();

        for (String typeOfWork : stock.getTypeOfWork()){
            sb.append(typeOfWork).append(";");
        }

        ps.setString(4, sb.toString());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            stock.setId(rs.getInt(1));
            return stock;
        }
        return null;
    }

    private Stock parseStock(ResultSet rs) throws SQLException {
        Stock stock = new Stock();
        stock.setId(rs.getInt("ID"));
        stock.setName(rs.getString("NAME"));
        stock.setDescription(rs.getString("DESCRIPTION"));
        stock.setDate(rs.getString("DATE"));
        stock.setParticipantOfStocks(DaoFactory.getFactory().getParticipantOfStackDao().getParticipantOfStock(stock.getId()));
        return stock;
    }


}
