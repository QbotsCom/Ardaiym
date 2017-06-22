package com.turlygazhy.command.impl.AdminCommands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.InfoMessageDao;
import com.turlygazhy.entity.InfoMessage;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lol on 08.06.2017.
 */

public class EditDescriptionCommand extends Command {
    private InfoMessage infoMessage;

    public EditDescriptionCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            bot.sendMessage(new SendMessage()
                    .setText("Choose Message")
                    .setChatId(chatId)
                    .setReplyMarkup(getKeyboard()));
            waitingType = WaitingType.CHOOSE;
            return false;
        }

        switch (waitingType) {
            case CHOOSE:
                infoMessage = infoMessageDao.getInfoMessage(Integer.parseInt(updateMessageText));
                sendMessage("Write new text", chatId, bot);
                waitingType = WaitingType.TEXT;
                return false;

            case TEXT:
                infoMessage.setText(updateMessageText);
                infoMessageDao.updateAbout(infoMessage);
                sendMessage("Done", chatId, bot);
                return true;

            case PHOTO:

                return true;
        }
        return false;
    }

    private ReplyKeyboard getKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(50));
        button.setCallbackData(String.valueOf(InfoMessageDao.ABOUT_ID));
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(51));
        button.setCallbackData(String.valueOf(InfoMessageDao.CONTACTS));
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText(buttonDao.getButtonText(53));
        button.setCallbackData(String.valueOf(InfoMessageDao.GROUP_ID));
        row.add(button);

        keyboardList.add(row);

        keyboard.setKeyboard(keyboardList);
        return keyboard;
    }
}
