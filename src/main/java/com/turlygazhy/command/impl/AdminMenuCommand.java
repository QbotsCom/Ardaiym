package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by lol on 05.06.2017.
 */

public class AdminMenuCommand extends Command {

    public AdminMenuCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (!userDao.isAdmin(chatId)){
            sendMessage(7, chatId, bot);
            return true;
        }
        sendMessage(6, chatId, bot);

        return false;
    }
}
