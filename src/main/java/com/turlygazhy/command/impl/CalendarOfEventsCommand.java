package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by daniyar on 22.06.17.
 */
public class CalendarOfEventsCommand extends com.turlygazhy.command.Command {
    private List<Stock> stocks;
    private Stock stock;

    protected CalendarOfEventsCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            stocks = stockDao.getAllStocks(false);
            if (stocks.size() == 0) {
                sendMessage(150, chatId, bot);  //  Ничего не запланированно
                return true;
            }
            StringBuilder sb = new StringBuilder();

            for (Stock stock : stocks) {
                sb.append("/id").append(stock.getId()).append(" - ")
                        .append(stock.getName()).append("\n");
//                                .append(stock.getDescription()).append("\n")
//                        .append(stock.getDate()).append("\n");
            }
            bot.sendMessage(new SendMessage()
                    .setChatId(chatId)
                    .setText(sb.toString())
                    .setReplyMarkup(keyboardMarkUpDao.select(9)));
            waitingType = WaitingType.CHOOSE;

            return false;
        }

        switch (waitingType) {
            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(5, chatId, bot);
                    return true;
                }
                stock = stockDao.getStock(Integer.parseInt(updateMessageText.substring(3)));
                String sb = "<b>" + messageDao.getMessageText(156) + "</b> " + stock.getName() + "\n" +
                        "<b>" + messageDao.getMessageText(157) + "</b> " + stock.getDescription() + "\n" +
                        "<b>" + messageDao.getMessageText(158) + "</b> " + stock.getDate();

                bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(sb)
                        .setParseMode(ParseMode.HTML));
                return false;
        }

        return false;
    }
}
