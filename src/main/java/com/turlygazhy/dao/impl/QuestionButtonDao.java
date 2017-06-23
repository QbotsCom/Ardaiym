package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.entity.QuestionButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 23.06.17.
 */
public class QuestionButtonDao extends AbstractDao {
    Connection connection;

    public QuestionButtonDao(Connection connection) {
        this.connection = connection;
    }

    public List<QuestionButton> getButtons(int questionId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUESTION_BUTTONS WHERE QUESTION_ID = ?");
        ps.setInt(1, questionId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        List<QuestionButton> buttons = new ArrayList<>();
        while (rs.next()) {
            buttons.add(parseQuestionButton(rs));
        }
        return buttons;
    }

    public QuestionButton insertButton(QuestionButton button) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("insert INTO QUESTION_BUTTONS (TEXT, QUESTION_ID) VALUES (?, ?)");
        ps.setString(1, button.getText());
        ps.setInt(2, button.getQuestionId());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            button.setId(rs.getInt(1));
            return button;
        }
        return null;
    }

    private QuestionButton parseQuestionButton(ResultSet rs) throws SQLException {
        QuestionButton button = new QuestionButton();
        button.setId(rs.getInt("ID"));
        button.setText(rs.getString("TEXT"));
        button.setQuestionId(rs.getInt("QUESTION_ID"));
        return button;
    }

    public void insertButtons(List<QuestionButton> questionButtonList) throws SQLException {
        for (QuestionButton button : questionButtonList){
            insertButton(button);
        }
    }
}
