package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.News;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lol on 08.06.2017.
 */
public class NewsDao extends AbstractDao {
    Connection connection;

    public NewsDao(Connection connection) {
        this.connection = connection;
    }

    public List<News> getNews() throws SQLException {
        List<News> news = new ArrayList<>();

        PreparedStatement ps = connection.prepareStatement("SELECT * FROM NEWS WHERE SHOW = TRUE");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            news.add(parseNews(rs));
        }
        return news;
    }

    public News getNews(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM NEWS WHERE ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseNews(rs);
        }
        return null;
    }

    private News parseNews(ResultSet rs) throws SQLException {
        News news = new News();
        news.setId(rs.getInt("ID"));
        news.setTitle(rs.getString("TITLE"));
        news.setText(rs.getString("TEXT"));
        news.setPhoto(rs.getString("PHOTO"));
        news.setDate(rs.getString("DATE"));
        news.setShow(rs.getBoolean("SHOW"));
        return news;
    }


    public void addNews(News news) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("INSERT INTO NEWS VALUES(default, ?, ?, ?, ?, default)");
        ps.setString(1, news.getTitle());
        ps.setString(2, news.getText());
        ps.setString(3, news.getDate());
        ps.setString(4, news.getPhoto());
        ps.execute();
    }

    public void updateNews(News news) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE NEWS SET TITLE = ?, TEXT = ?, PHOTO = ?, SHOW = ? WHERE ID = ?");
        ps.setString(1, news.getTitle());
        ps.setString(2, news.getText());
        ps.setString(3,news.getPhoto());
        ps.setBoolean(4, news.isShow());
        ps.setInt(5, news.getId());
        ps.execute();
    }
}
