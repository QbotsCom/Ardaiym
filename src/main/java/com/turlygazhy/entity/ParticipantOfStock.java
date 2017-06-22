package com.turlygazhy.entity;

import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.MessageDao;

import java.sql.SQLException;

/**
 * Created by lol on 06.06.2017.
 */
public class ParticipantOfStock {
    int id;
    User user;
    private int stockId;
    private String typeOfWork;
    private boolean finished;
    private String report;

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getStockId() {
        return stockId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTypeOfWork() {
        return typeOfWork;
    }

    public void setTypeOfWork(String typeOfWork) {
        this.typeOfWork = typeOfWork;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        MessageDao messageDao = DaoFactory.getFactory().getMessageDao();
        try {
            sb.append("<b>").append(messageDao.getMessageText(60)).append("</b> ").append(user.getName()).append("\n");
            sb.append("<b>").append(messageDao.getMessageText(123)).append("</b> ").append(typeOfWork).append("\n");
            sb.append("<b>").append(messageDao.getMessageText(61)).append("</b> ");

            if (finished){
                sb.append(messageDao.getMessageText(64)).append("\n");
                sb.append("<b>").append(messageDao.getMessageText(62)).append("</b>").append(report).append("\n");
            } else {
                sb.append(messageDao.getMessageText(63)).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
