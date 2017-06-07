package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Stock;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by lol on 05.06.2017.
 */
public class AddToParticipantOfStock extends Command {

    public AddToParticipantOfStock() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        String data = update.getCallbackQuery().getData();
        String stockIdString = data.substring(data.indexOf("id") + 2, data.indexOf(" cmd"));
        int stockId = Integer.valueOf(stockIdString);
        Stock stock = stockDao.getStock(stockId);
        String typeOfWork = data.substring(0, data.indexOf("id"));

        Long userId = Long.valueOf(update.getCallbackQuery().getFrom().getId());
        participantOfStockDao.addParticipantToStock(stockId, userId, typeOfWork);
        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(messageDao.getMessageText(29)).append("</b>\n")
                .append(stock.getName()).append("\n")
                .append("<b>").append(messageDao.getMessageText(28)).append("</b> ").append(typeOfWork);

        bot.sendMessage(new SendMessage()
                .setChatId(userId)
                .setText(sb.toString())
                .setParseMode(ParseMode.HTML));

        return true;
    }
}
