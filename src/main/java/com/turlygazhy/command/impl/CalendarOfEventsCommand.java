package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.WaitingType;
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
            StringBuilder sb = new StringBuilder();
            for (Stock stock : stocks) {
                sb.append("/id").append(stock.getId()).append(" - ")
                        .append(stock.getName()).append("\n");
//                                .append(stock.getDescription()).append("\n")
//                        .append(stock.getDate()).append("\n");
            }
            sendMessage(sb.toString(), chatId, bot);
            waitingType = WaitingType.CHOOSE;

            return false;
        }

        switch (waitingType){
            case CHOOSE:
                stock = stockDao.getStock(Integer.parseInt(updateMessageText.substring(3)));
                String sb = "Title: " + stock.getName() + "\n" +
                        "Description: " + stock.getDescription() + "\n" +
                        "Date: " + stock.getDate();

                sendMessage(sb, chatId, bot);
                return false;
        }

        return false;
    }
}
