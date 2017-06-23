package com.turlygazhy.command.impl.AdminCommands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by daniyar on 23.06.17.
 */
public class SurveyMenuCommand extends Command {
    List<Survey> surveys;
    Survey survey;

    public SurveyMenuCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            sendMessage(145, chatId, bot);
            waitingType = WaitingType.COMMAND;
            return false;
        }

        switch (waitingType) {
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(104))) {   // Статистика опросов
                    sendSurveys(bot);
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(6, chatId, bot);
                    return true;
                }

                return false;
            case CHOOSE:
                survey = surveyDao.getSurvey(Integer.parseInt(updateMessageText.substring(3)));
                sendStatistic(survey, bot);
                return false;
        }

        return false;
    }

    private void sendStatistic(Survey survey, Bot bot) throws SQLException, TelegramApiException {
        StringBuilder sb = new StringBuilder();

        sb.append("<b>").append(messageDao.getMessageText(140)).append("</b> ").append(survey.getText()).append("\n");

        for (Question question : survey.getQuestions()) {

            int[] count = new int[question.getQuestionButtonList().size()];
            StringBuilder otherAnswers = new StringBuilder();
            sb.append("<b>").append(messageDao.getMessageText(141)).append("</b> ").append(question.getText()).append("\n");
            for (Answer answer : question.getAnswers()) {
                int answerIndex = getIndex(answer, question);
                count[answerIndex]++;
                if (answerIndex == count.length - 1) {
                    otherAnswers.append(answer.getText()).append("\n");
                }
            }
            sb.append("<b>").append(messageDao.getMessageText(142)).append("</b>\n");
            for (int i = 0; i < question.getQuestionButtonList().size(); i++) {

                sb.append("\t\t").append(question.getQuestionButtonList().get(i).getText()).append(" - ").append(count[i]).append("\n");
            }
            sb.append(otherAnswers).append("\n\n");
        }

        bot.sendMessage(new SendMessage()
                .setText(sb.toString())
                .setChatId(chatId)
                .setParseMode(ParseMode.HTML));
    }

    private int getIndex(Answer answer, Question question) {
        for (int i = 0; i < question.getQuestionButtonList().size(); i++) {
            QuestionButton button = question.getQuestionButtonList().get(i);
            if (answer.getText().equals(button.getText()))
                return i;
        }
        return question.getQuestionButtonList().size() - 1;
    }

    private void sendSurveys(Bot bot) throws SQLException, TelegramApiException {
        surveys = surveyDao.getSurveys();
        StringBuilder sb = new StringBuilder();
        for (Survey survey : surveys) {
            sb.append("/id").append(survey.getId()).append(" - ").append(survey.getText()).append("\n");
        }
        sendMessage(sb.toString(), chatId, bot);
    }
}
