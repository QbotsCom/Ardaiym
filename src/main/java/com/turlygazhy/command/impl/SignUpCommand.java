package com.turlygazhy.command.impl;

import com.sun.xml.internal.ws.api.ha.StickyFeature;
import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.*;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lol on 02.06.2017.
 */

public class SignUpCommand extends Command {
    User user = new User();

    protected SignUpCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            user.setChatId(chatId);
            sendMessage(11, chatId, bot);       // Введите ФИО
            waitingType = WaitingType.NAME;
            return false;
        }

        switch (waitingType) {
            case NAME:
                user.setName(updateMessageText);
                sendMessage(12, chatId, bot);   // Отправьте контакт
                waitingType = WaitingType.CONTACT;
                return false;

            case CONTACT:
                if (updateMessage.getContact() != null) {
                    user.setPhoneNumber(updateMessage.getContact().getPhoneNumber());
                } else {
                    user.setPhoneNumber(updateMessageText);
                }
                sendMessage(13, chatId, bot);   // Введите город
                waitingType = WaitingType.CITY;
                return false;

            case CITY:
                user.setCity(updateMessageText);
                sendMessage(14, chatId, bot);   // Введите пол
                waitingType = WaitingType.SEX;
                return false;

            case SEX:
                if (updateMessageText.equals(buttonDao.getButtonText(20))) {
                    user.setSex(false);
                } else {
                    user.setSex(true);
                }
                sendMessage(15, chatId, bot);   // Введите дату рождения
                waitingType = WaitingType.BIRTHDAY;
                return false;

            case BIRTHDAY:
                user.setBirthday(updateMessageText);
                sendMessage(16, chatId, bot);   // У вас есть машина?
                waitingType = WaitingType.HAVE_CAR;
                return false;

            case HAVE_CAR:
                if (updateMessageText.equals(buttonDao.getButtonText(22))) {
                    user.setHaveCar(true);
                } else {
                    user.setHaveCar(false);
                }
                sendMessage(17, chatId, bot);   // Ваша кандидатура на рассмотрении
                userDao.addUser(user);
                user = userDao.getUserByChatId(user.getChatId());

                bot.sendMessage(new SendMessage()
                        .setChatId(groupDao.select(1).getChatId())
                        .setText(messageDao.getMessageText(18) + user.getName())
                        .setReplyMarkup(getInlineKeyboard()));
                return true;

        }
        return false;
    }

    private ReplyKeyboard getInlineKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(new InlineKeyboardButton()
                .setText(buttonDao.getButtonText(24))
                .setCallbackData(String.valueOf(user.getId()) + " cmd=" + buttonDao.getButtonText(24)));
        buttons.add(row);
        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }
}
