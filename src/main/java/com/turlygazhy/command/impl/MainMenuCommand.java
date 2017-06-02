package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by lol on 02.06.2017.
 */
public class MainMenuCommand extends Command {

    protected MainMenuCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        sendMessage(5, chatId, bot);        // Главное меню
        waitingType = WaitingType.COMMAND;

//        Message message;
//        message = messageDao.getMessage(messageId);
//        SendPhoto sendPhoto = message.getSendPhoto();
//        SendMessage sendMessage = message.getSendMessage();
//        if (sendPhoto != null) {
//            try {
//                bot.sendPhoto(sendPhoto.setChatId(chatId));
//            } catch (Exception e) {
//                sendMessage("<i>cannot send photo</i>", chatId, bot);
//            }
//        }
//        bot.sendMessage(sendMessage
//                .setChatId(chatId)
//                .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()))
//        );
//
        return false;
    }
}
