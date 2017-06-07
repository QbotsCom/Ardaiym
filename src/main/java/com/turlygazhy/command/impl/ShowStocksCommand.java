package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.ParticipantOfStock;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by lol on 06.06.2017.
 */
public class ShowStocksCommand extends Command {
    List<Stock> stocks;
    Stock stock;
    int stockIndex = 0;

    public ShowStocksCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            sendMessage(50, chatId, bot);
            waitingType = WaitingType.COMMAND;
            return false;
        }

        switch (waitingType) {
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {     // Назад
                    sendMessage(6, chatId, bot);
                    return true;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(45))) {    // Все акции
                    stocks = stockDao.getAllStocks();
                    StringBuilder sb = new StringBuilder();
                    for (Stock stock : stocks) {
                        sb.append("/id").append(stock.getId()).append(" ").append(stock.getName()).append("\n");
                    }
                    sendMessage(sb.toString(), chatId, bot);
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(46))) {    // Предстоящие акции
                    stocks = stockDao.getAllStocks();
                    StringBuilder sb = new StringBuilder();
                    Date date = new Date();
                    int dateInt = date.getDate() + (date.getMonth() + 1) * 100;

                    for (Stock stock : stocks) {
                        int stockDate;
                        String dateToInt = stock.getDate();
                        String dates = dateToInt.substring(0, dateToInt.indexOf("."));
                        stockDate = Integer.parseInt(dates);
                        dates = dateToInt.substring(dateToInt.indexOf(".") + 1);
                        stockDate += Integer.parseInt(dates) * 100;

                        if (stockDate > dateInt) {
                            sb.append("/id").append(stock.getId()).append(" ").append(stock.getName()).append("\n");
                        }
                    }
                    sendMessage(sb.toString(), chatId, bot);
                    return false;
                }

                if (updateMessageText.startsWith("/id")) {
                    int stockId = Integer.parseInt(updateMessageText.substring(3));
                    for (int i = 0; i < stocks.size(); i++) {
                        if (stocks.get(i).getId() == stockId) {
                            stock = stocks.get(i);
                            stockIndex = i;
                            break;
                        }
                    }
                    bot.sendMessage(new SendMessage()
                            .setText(stock.toString())
                            .setChatId(chatId)
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkUpDao.select(16)));
                    waitingType = WaitingType.STOCK_COMMAND;
                    return false;
                }
                return false;

            case STOCK_COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {     // Назад
                    sendMessage(50, chatId, bot);
                    waitingType = WaitingType.COMMAND;
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(60))) {    // Следующая акция
                    stockIndex++;
                    if (stocks.size() >= stockIndex) {
                        stockIndex--;
                    }
                    stock = stocks.get(stockIndex);
                    bot.sendMessage(new SendMessage()
                            .setText(stock.toString())
                            .setChatId(chatId)
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkUpDao.select(16)));
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(61))) {     // Предыдущая акция
                    stockIndex--;
                    if (stocks.size() < 0) {
                        stockIndex++;
                    }
                    stock = stocks.get(stockIndex);
                    bot.sendMessage(new SendMessage()
                            .setText(stock.toString())
                            .setChatId(chatId)
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkUpDao.select(16)));
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(62))) {    // Отчет о проделанной работе
                    StringBuilder sb = new StringBuilder();
                    for (ParticipantOfStock participantOfStock : stock.getParticipantOfStocks()) {
                        sb.append("<b>").append(messageDao.getMessageText(60)).append(" </b>")  // Ответственный
                                .append(participantOfStock.getUser().getName()).append("\n")
                                .append(participantOfStock.getTypeOfWork()).append("\n")
                                .append("<b>").append(messageDao.getMessageText(61)).append(" </b>");   // Статус
                        if (participantOfStock.isFinished()) {
                            sb.append(messageDao.getMessageText(64)).append("\n")               // Выполнено
                                    .append("<b>").append(messageDao.getMessageText(62)).append(" </b>")    // Отчет
                                    .append(participantOfStock.getReport()).append("\n");
                        } else {
                            sb.append(messageDao.getMessageText(63)).append("\n");               // Не выполнено
                        }
                        sb.append("\n");
                    }
                    bot.sendMessage(new SendMessage()
                            .setChatId(chatId)
                            .setText(sb.toString())
                            .setParseMode(ParseMode.HTML));
                    return false;
                }
                return false;
        }

        return false;
    }
}
