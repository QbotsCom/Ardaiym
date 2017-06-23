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
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 23.06.17.
 */
public class NewSurveyCommand extends Command {
    private Survey survey;
    private Question question;
    private QuestionButton questionButton;

    public NewSurveyCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            survey = new Survey();
            sendMessage(130, chatId, bot);  // Введите название опроса
            waitingType = WaitingType.SURVEY_NAME;
            return false;
        }

        switch (waitingType) {
            case SURVEY_NAME:
                survey.setText(updateMessageText);
                sendMessage(131, chatId, bot);  // Введите вопрос
                waitingType = WaitingType.QUESTION;
                return false;

            case QUESTION:
                if (updateMessageText.equals(buttonDao.getButtonText(41))) {    // Сохранить опрос
                    sendMessage(133, chatId, bot);  // Сохранение опроса...
                    surveyDao.insertSurvey(survey);
                    sendMessage(134, chatId, bot);  // Опрос сохранен!
//                    sendMessage(135, chatId, bot);  // Разослать его волонтерам?
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                question = new Question();
                question.setText(updateMessageText);
                sendMessage(132, chatId, bot);  // Введите варианты ответа
                waitingType = WaitingType.QUESTION_BUTTON;
                return false;

            case QUESTION_BUTTON:
                if (updateMessageText.equals(buttonDao.getButtonText(39))) {    // Следующий вопрос
                    survey.addQuestion(question);
                    sendMessage(131, chatId, bot);  // Введите вопрос
                    waitingType = WaitingType.QUESTION;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(41))) {
                    if (question.getQuestionButtonList() != null) {
                        survey.addQuestion(question);
                    }
                    sendMessage(133, chatId, bot);  // Сохранение опроса...
                    surveyDao.insertSurvey(survey);
                    sendMessage(134, chatId, bot);  // Опрос сохранен!
                    SendMessage message = new SendMessage()
                            .setText(survey.getText())
                            .setReplyMarkup(getSurveyKeyboard());

                    for (User user : userDao.getUsers()) {
                        try {
                            bot.sendMessage(message.setChatId(user.getChatId()));
                        } catch (TelegramApiRequestException ex) {
                            ex.printStackTrace();
                            sendMessage("BAN FROM: " + user.getName(), chatId, bot);
                        }
                    }
                    sendMessage(136, chatId, bot);  // Опрос разослан!
//                    sendMessage(135, chatId, bot);  // Разослать его волонтерам?
//                    waitingType = WaitingType.CHOOSE;
                    return true;
                }
                questionButton = new QuestionButton();
                questionButton.setText(updateMessageText);
                question.addQuestionButton(questionButton);
                return false;
//            case CHOOSE:
//                if (updateMessageText.equals(buttonDao.getButtonText(22))) {    // Да
//                    SendMessage message = new SendMessage()
//                            .setText(survey.getText())
//                            .setReplyMarkup(getSurveyKeyboard());
//
//                    for (User user : userDao.getUsers()) {
//                        try {
//                            bot.sendMessage(message.setChatId(user.getChatId()));
//                        } catch (TelegramApiRequestException ex) {
//                            ex.printStackTrace();
//                            sendMessage("BAN FROM: " + user.getName(), chatId, bot);
//                        }
//                    }
//                    sendMessage(136, chatId, bot);  // Опрос разослан!
//                }
//                if (updateMessageText.equals(buttonDao.getButtonText(23))) {    // нет
//                    sendMessage(137, chatId, bot);  // Опрос не будет разослан
//                }
//                return true;
        }

        return true;
    }

    private ReplyKeyboard getSurveyKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(100));
        button.setCallbackData("survey=" + survey.getId() + " cmd=SurveyCommand");
        row.add(button);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
