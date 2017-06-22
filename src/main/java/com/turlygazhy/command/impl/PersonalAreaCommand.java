package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.dao.impl.ParticipantOfStockDao;
import com.turlygazhy.entity.ParticipantOfStock;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.WaitingType;
import javafx.concurrent.Task;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by daniyar on 21.06.17.
 */
public class PersonalAreaCommand extends com.turlygazhy.command.Command {
    ParticipantOfStock participantOfStock;
    Stock stock;

    public PersonalAreaCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            sendMessage(120, chatId, bot);
            waitingType = WaitingType.COMMAND;
            return false;
        }

        if (updateMessageText.equals(buttonDao.getButtonText(93))) {
            sendMessage("Write report", chatId, bot);
            waitingType = WaitingType.MESSAGE;
            return false;
        }

        switch (waitingType) {
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(90))) {
                    sendMessage(121, chatId, bot);
                    waitingType = WaitingType.CHOOSE_TYPE_OF_WORK;
                    return false;
                }
                return false;

            case CHOOSE_TYPE_OF_WORK:
                List<ParticipantOfStock> participantOfStockList = null;
                if (updateMessageText.equals(buttonDao.getButtonText(91))) {
                    participantOfStockList = participantOfStockDao.getParticipantOfStock(chatId, true);
                    waitingType = WaitingType.CHOOSE_TASK;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(92))) {
                    participantOfStockList = participantOfStockDao.getParticipantOfStock(chatId, false);
                    waitingType = WaitingType.CHOOSE_TASK;
                }
                StringBuilder sb = new StringBuilder();
                assert participantOfStockList != null;
                for (ParticipantOfStock stock : participantOfStockList) {
                    sb.append("/id").append(stock.getId()).append(" - ").append(stock.getTypeOfWork()).append("\n");
                }
                sendMessage(sb.toString(), chatId, bot);
                waitingType = WaitingType.CHOOSE_TASK;
                return false;

            case CHOOSE_TASK:
                int stockId = Integer.parseInt(updateMessageText.substring(3));
                participantOfStock = participantOfStockDao.getParticipantOfStockById(stockId);
                stock = stockDao.getStock(participantOfStock.getStockId());

                sb = new StringBuilder();
                sb.append("<b>").append(messageDao.getMessageText(122)).append("</b>").append(stock.getName()).append("\n");
                sb.append(participantOfStock.toString());
                if (participantOfStock.isFinished()) {
                    bot.sendMessage(new SendMessage()
                            .setChatId(chatId)
                            .setText(sb.toString())
                            .setParseMode(ParseMode.HTML));
                    waitingType = WaitingType.CHOOSE_TYPE_OF_WORK;
                } else {

                    bot.sendMessage(new SendMessage()
                            .setChatId(chatId)
                            .setText(sb.toString())
                            .setReplyMarkup(keyboardMarkUpDao.select(22))
                            .setParseMode(ParseMode.HTML));
                }
                return false;


            case MESSAGE:
                participantOfStock.setFinished(true);
                participantOfStock.setReport(updateMessageText);
                participantOfStockDao.updateParticipantOFStock(participantOfStock);
                sendMessage("done", chatId, bot);
                sb = new StringBuilder();
                sb.append("<b>").append(messageDao.getMessageText(122)).append("</b>").append(stock.getName()).append("\n");
                sb.append(participantOfStock.toString());

                bot.sendMessage(new SendMessage()
                        .setText(sb.toString())
                        .setChatId(stockDao.getStock(participantOfStock.getStockId()).getAddedBy().getChatId())
                        .setParseMode(ParseMode.HTML));

                waitingType = WaitingType.CHOOSE_TASK;
                return false;
        }
        return false;
    }
}
