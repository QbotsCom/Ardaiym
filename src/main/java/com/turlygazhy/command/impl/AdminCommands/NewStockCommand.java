package com.turlygazhy.command.impl.AdminCommands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.GroupDao;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lol on 05.06.2017.
 */

public class NewStockCommand extends Command {
    private Stock stock;
    private int shownDates = 0;

    public NewStockCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            sendMessage(20, chatId, bot);           // Введите название акции
            waitingType = WaitingType.NAME;
            stock = new Stock();
            return false;
        }

        switch (waitingType) {
            case NAME:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(7, chatId, bot);
                    return true;
                }
                stock.setName(updateMessage.getText());
                sendMessage(21, chatId, bot);       // Введите описание
                waitingType = WaitingType.DESCRIPTION;
                return false;

            case DESCRIPTION:
                stock.setDescription(updateMessageText);
                sendMessage(24, chatId, bot);       // Перечислите виды работ
                waitingType = WaitingType.TYPE_OF_WORK;
                return false;

            case TYPE_OF_WORK:
                if (updateMessageText.equals(buttonDao.getButtonText(12))) {     // Готово
                    if (stock.getTypeOfWork().size() == 0){
                        sendMessage(154, chatId, bot);  // Введите хотя бы 1 вид работ
                    }
                    waitingType = WaitingType.DATE;
                    sendMessage(22, getDeadlineKeyboard(shownDates));       // Назначьте дату акции
                    return false;
                }
                stock.addTypeOfWork(updateMessageText);
                return false;

            case DATE:
                if (updateMessageText.equals(nextText)) {
                    shownDates++;
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(22))     // Введите дедлайн
                            .setReplyMarkup(getDeadlineKeyboard(shownDates))
                    );
                    return false;
                }

                if (updateMessageText.equals(prevText)) {
                    shownDates--;
                    bot.editMessageText(new EditMessageText()
                            .setMessageId(updateMessage.getMessageId())
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(22))     // Введите дедлайн
                            .setReplyMarkup(getDeadlineKeyboard(shownDates))
                    );
                    return false;
                }
                stock.setDate(updateMessageText);
                stock.setAddedBy(userDao.getUserByChatId(chatId));
                stock = stockDao.addStock(stock);
                sendMessage(23, chatId, bot);

                bot.sendMessage(new SendMessage()
                        .setText(buttonDao.getButtonText(30) + ": " + stock.toString())        // Новая акция:
                        .setChatId(GroupDao.GROUP_ID)
                        .setParseMode(ParseMode.HTML)
                        .setReplyMarkup(getInlineKeyboard()));

                waitingType = null;
                return true;
        }

        return false;
    }

    private InlineKeyboardMarkup getDeadlineKeyboard(int shownDates) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        Date date = new Date();
        date.setDate(date.getDate() + (shownDates * 9));
        List<InlineKeyboardButton> row = null;
        for (int i = 1; i < 10; i++) {
            if (row == null) {
                row = new ArrayList<>();
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            int dateToString = date.getDate();
            String stringDate;
            if (dateToString > 9) {
                stringDate = String.valueOf(dateToString);
            } else {
                stringDate = "0" + dateToString;
            }
            int monthToString = date.getMonth() + 1;
            String stringMonth;
            if (monthToString > 9) {
                stringMonth = String.valueOf(monthToString);
            } else {
                stringMonth = "0" + monthToString;
            }
            String dateText = stringDate + "." + stringMonth;
            button.setText(dateText);
            button.setCallbackData(dateText);
            row.add(button);
            if (i % 3 == 0) {
                rows.add(row);
                row = null;
            }
            date.setDate(date.getDate() + 1);
        }

        if (shownDates > 0) {
            rows.add(getNextPrevRows(true, true));
        } else {
            rows.add(getNextPrevRows(false, true));
        }


        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private InlineKeyboardMarkup getInlineKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (String typeOfWork : stock.getTypeOfWork()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(typeOfWork);
            button.setCallbackData(typeOfWork + "id" + stock.getId() + " cmd=" + buttonDao.getButtonText(40));
            row.add(button);
            buttons.add(row);
        }
        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }
}
