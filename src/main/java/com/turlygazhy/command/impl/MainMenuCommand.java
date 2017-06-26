package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by lol on 02.06.2017.
 */

public class MainMenuCommand extends Command {

    private Command command;

    protected MainMenuCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (command != null) {
            if (command.execute(update, bot)) {
                command = null;
            }
            return false;
        }

        if (waitingType == null) {
            sendMessage(5, chatId, bot);        // Главное меню
            waitingType = WaitingType.COMMAND;
            return false;
        }

        switch (waitingType) {
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(50))) {     // О нас
                    sendMessage(MessageDao.ABOUT_ID, chatId, bot);
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(51))) {     // Контакты
                    sendMessage(MessageDao.CONTACTS, chatId, bot);
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(52))) {     // Личный кабинет
                    command = new PersonalAreaCommand();
                    if (command.execute(update, bot)) {
                        command = null;
                    }
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(53))) {     // Группа
                    sendMessage(MessageDao.GROUP_ID, chatId, bot);
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(54))) {     // Календарь событий
                    command = new CalendarOfEventsCommand();
                    if (command.execute(update, bot)) {
                        command = null;
                    }
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(55))) {     // Новости
                    sendMessage(70, chatId, bot);
                    return false;
                }
                return false;

        }

        return false;
    }
}
