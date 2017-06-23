package com.turlygazhy.command.impl.AdminCommands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by daniyar on 23.06.17.
 */
public class SurveyCommand extends Command {
    private Survey survey;
    private ListIterator<Question> iterator;
    private Question question;

    public SurveyCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            String data = update.getCallbackQuery().getData();
            int surveyId = Integer.parseInt(data.substring(data.indexOf("survey=") + 7, data.indexOf(" ")));
            survey = surveyDao.getSurvey(surveyId);
            iterator = survey.getQuestions().listIterator();
            sendQuestion(bot);
            waitingType = WaitingType.ANSWER;
            return false;
        }

        switch (waitingType) {
            case ANSWER:
                if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(38))) {
                    sendMessage(138, chatId, bot);  // Введите свой ответ
                    waitingType = WaitingType.TEXT;
                    return false;
                }
                insertAnswer(update.getCallbackQuery().getData());
                return !sendQuestion(bot);

            case TEXT:
                insertAnswer(updateMessageText);
                if (sendQuestion(bot)){
                    waitingType = WaitingType.ANSWER;
                    return false;
                }
                return true;

        }

        return true;
    }

    private void insertAnswer(String data) throws SQLException, TelegramApiException {
        Answer answer = new Answer();
        answer.setText(data);
        answer.setQuestionId(question.getId());
        answer = answerDao.insertAnswer(answer);
        question.addAnswer(answer);
    }

    private boolean sendQuestion(Bot bot) throws TelegramApiException, SQLException {
        if (!iterator.hasNext()){
            sendMessage(139, chatId, bot);  // Опрос окончен
            return false;
        }
        question = iterator.next();
        bot.sendMessage(new SendMessage()
                .setText(question.getText())
                .setChatId(chatId)
                .setReplyMarkup(getReplyKeyboard(question.getQuestionButtonList())));
        return true;
    }

    private ReplyKeyboard getReplyKeyboard(List<QuestionButton> buttons) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
        for (QuestionButton button : buttons) {
            List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(button.getText());
            inlineKeyboardButton.setCallbackData(button.getText());
            inlineKeyboardButtons.add(inlineKeyboardButton);
            buttonList.add(inlineKeyboardButtons);
        }
        keyboardMarkup.setKeyboard(buttonList);
        return keyboardMarkup;
    }
}
