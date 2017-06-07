package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lol on 05.06.2017.
 */
public class NewDistributionCommand extends Command {
    public NewDistributionCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            sendMessage(40, chatId, bot);
            waitingType = WaitingType.MESSAGE;
            return false;
        }

        switch (waitingType) {
            case MESSAGE:
                sendMessage(88, chatId, bot);
                List<User> users = userDao.getUsers();
                for (User user : users) {
                    try {
                        sendMessage(updateMessageText, user.getChatId(), bot);
                    } catch (Exception ex) {
                        sendMessage("BAN FROM: " + user.getName(), chatId, bot);
                    }
                }
                sendMessage(49, chatId, bot);
                return true;
        }

        return false;
    }
}
