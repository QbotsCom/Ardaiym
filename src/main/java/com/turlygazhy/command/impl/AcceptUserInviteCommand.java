package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
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

        if (updateMessageText.equals(buttonDao.getButtonText(24))) {
            String userId = updateMessage.getText().substring(11);
            userId = userId.substring(3, userId.indexOf(" "));
            System.out.println(userId);
            User user = userDao.getUserByUserId(Long.valueOf(userId));
            user.setAdded(true);
//            user.setAddedBy(Long.valueOf(update.getMessage().)); // todo взять пользователя, который принял нового пользователя
            userDao.updateUser(user);

            sendMessage(88, user.getAddedBy(), bot);
            sendMessage(19, user.getChatId(), bot);

            bot.editMessageText(new EditMessageText()
                    .setText("OK")
                    .setChatId(chatId)
                    .setMessageId(updateMessage.getMessageId()));
        }

        return false;
    }
}
