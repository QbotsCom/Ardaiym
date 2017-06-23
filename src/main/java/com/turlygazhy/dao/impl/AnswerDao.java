package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.Answer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 23.06.17.
 */
public class AnswerDao extends AbstractDao {
    Connection connection;

    public AnswerDao(Connection connection) {
        this.connection = connection;
    }

    public List<Answer> getAnswers(int questionId) throws SQLException {
        List<Answer> answers = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM SURVEY_ANSWERS WHERE QUESTION_ID = ?");
        ps.setInt(1, questionId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            answers.add(parseAnswer(rs));
        }
        return answers;
    }

    public Answer insertAnswer(Answer answer) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO SURVEY_ANSWERS (TEXT, QUESTION_ID) VALUES (?, ?)");
        ps.setString(1, answer.getText());
        ps.setInt(2, answer.getQuestionId());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            answer.setId(rs.getInt(1));
        }
        return answer;
    }

    private Answer parseAnswer(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setId(rs.getInt("ID"));
        answer.setText(rs.getString("TEXT"));
        answer.setQuestionId(rs.getInt("QUESTION_ID"));
        return answer;
    }

}
