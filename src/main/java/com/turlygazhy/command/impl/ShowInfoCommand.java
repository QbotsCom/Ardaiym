package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by user on 5/27/17.
 */

public class ShowInfoCommand extends Command {
    private SignUpCommand signUpCommand;
    private MainMenuCommand mainMenuCommand;

    public ShowInfoCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        User user;

        initMessage(update, bot);

        if (updateMessage.isGroupMessage()){
            return true;
        }

        if (signUpCommand != null) {
            return signUpCommand.execute(update, bot);
        }

        if (mainMenuCommand != null) {
            return mainMenuCommand.execute(update, bot);
        }
        user = userDao.getUserByChatId(chatId);
        if (user == null) {
            sendMessage(10, chatId, bot);   // Чтобы продолжить, нужно зарегистрироваться
            signUpCommand = new SignUpCommand();
            return signUpCommand.execute(update, bot);
        }
        if (user.isAdded()) {
            mainMenuCommand = new MainMenuCommand();
            return mainMenuCommand.execute(update, bot);
        }
        return true;

    }
}