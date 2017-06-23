package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.entity.Question;
import com.turlygazhy.entity.Survey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 23.06.17.
 */
public class SurveyDao extends AbstractDao {
    Connection connection;

    public SurveyDao(Connection connection) {
        this.connection = connection;
    }

    public Survey getSurvey(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM SURVEY WHERE ID = ?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()){
            return parseSurvey(rs);
        }
        return null;
    }

    public void insertSurvey(Survey survey) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("INSERT INTO SURVEY (TEXT) VALUES (?)");
        ps.setString(1, survey.getText());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()){
            survey.setId(rs.getInt(1));
        }
        for (Question question : survey.getQuestions()){
            question.setSurveyId(survey.getId());
        }
        DaoFactory.getFactory().getQuestionDao().insertQuestions(survey.getQuestions());
    }

    private Survey parseSurvey(ResultSet rs) throws SQLException {
        Survey survey = new Survey();
        survey.setId(rs.getInt("ID"));
        survey.setText(rs.getString("TEXT"));
        List<Question> questions = DaoFactory.getFactory().getQuestionDao().getQuestion(survey.getId());
        survey.setQuestions(questions);
        return survey;
    }

    public List<Survey> getSurveys() throws SQLException {
        List<Survey> surveys = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM SURVEY");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            surveys.add(parseSurvey(rs));
        }
        return surveys;
    }
}
