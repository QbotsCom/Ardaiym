package com.turlygazhy.command.impl.AdminCommands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 20.06.17.
 */
public class AddAdminCommand extends Command {
    private int page = 0;
    private List<User> users;

    public AddAdminCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            bot.sendMessage(new SendMessage()
                    .setChatId(chatId)
                    .setText("Choose user")
                    .setReplyMarkup(chooseUserKeyboard(users, page)));
            waitingType = WaitingType.CHOOSE;
            return false;
        }

        switch (waitingType) {
            case CHOOSE:
                if (updateMessageText.equals(messageDao.getMessageText(89))) {
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setText("Choose user")
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) chooseUserKeyboard(users, ++page)));
                    return false;
                }
                if (updateMessageText.equals(messageDao.getMessageText(90))) {
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setText("Choose user")
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) chooseUserKeyboard(users, --page)));
                    return false;
                }
                Long userId = Long.valueOf(updateMessageText);
                User user = userDao.getUserByChatId(userId);
                user.setRules(2);
                userDao.updateUser(user);
                try{
                    sendMessage("Now you are admin", user.getChatId(), bot);
                } catch (Exception ex){
                    sendMessage("BAN FROM: " + user.getName(), chatId, bot);
                }
                sendMessage("Done", chatId, bot);
                return true;
        }

        return false;
    }

    private ReplyKeyboard chooseUserKeyboard(List<User> users, int i) throws SQLException {
        if (users == null) {
            users = userDao.getUsers(1);
        }
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (int k = i * 6; k < i * 6 + 6 && users.size() > k; k++) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(users.get(k).getName());
            button.setCallbackData(String.valueOf(users.get(k).getChatId()));
            buttons.add(button);
            row.add(buttons);
        }
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        if (i != 0) {
            InlineKeyboardButton prev = new InlineKeyboardButton();
            prev.setText(messageDao.getMessageText(89));
            prev.setCallbackData(messageDao.getMessageText(89));
            buttons.add(prev);
        }
        if (users.size() > i*6+6) {
            InlineKeyboardButton next = new InlineKeyboardButton();
            next.setText(messageDao.getMessageText(90));
            next.setCallbackData(messageDao.getMessageText(90));
            buttons.add(next);
        }
        row.add(buttons);

        keyboard.setKeyboard(row);
        return keyboard;
    }
}
