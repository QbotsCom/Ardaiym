package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by lol on 02.06.2017.
 */
public class AcceptUserInviteCommand extends Command {
    public AcceptUserInviteCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        Long addedBy = update.getCallbackQuery().getFrom().getId().longValue();
        sendMessage(88, addedBy, bot);    // Ждите...

        String userId = update.getCallbackQuery().getData();
        User user = userDao.getUserByUserId(Long.valueOf(userId.substring(0, userId.indexOf(" "))));
        user.setAdded(true);
        user.setAddedBy(addedBy);
        userDao.updateUser(user);

        sendMessage(19, user.getChatId(), bot);     // Ваша кондитатура одобрена
        sendMessage("user " + user.getName() + "has added", addedBy, bot);


        bot.editMessageText(new EditMessageText()
                .setText("OK")
                .setChatId(chatId)
                .setMessageId(updateMessage.getMessageId()));

        return true;

    }
}
