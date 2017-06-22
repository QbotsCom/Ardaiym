package com.turlygazhy.command.impl.AdminCommands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.ParticipantOfStock;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import javassist.bytecode.ExceptionsAttribute;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by lol on 06.06.2017.
 */
public class ShowStocksCommand extends Command {
    private List<Stock> stocks;
    private Stock stock;

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
                    stocks = stockDao.getAllStocks(false);
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
                    for (Stock stock1 : stocks) {
                        if (stock1.getId() == stockId) {
                            stock = stock1;
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

                if (updateMessageText.equals(buttonDao.getButtonText(60))) {    // Завершить акцию
                    stock.setFinished(true);
                    stockDao.updateStock(stock);
                    sendMessage("Stock was finished", chatId, bot);
                    List<User> users = new ArrayList<>();
                    for (ParticipantOfStock participantOfStock : stock.getParticipantOfStocks()) {
                        if (!userContains(participantOfStock.getUser(), users)) {
                            users.add(participantOfStock.getUser());
                        }
                    }
                    sendSurvey(users, bot);
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

    private void sendSurvey(List<User> users, Bot bot) throws SQLException, TelegramApiException {
        ReplyKeyboard keyboardMarkup = getSurveyKeyboard();
        for (User user : users) {
            try {
//                sendMessage("Survey", user.getChatId(), bot);
                bot.sendMessage(new SendMessage()
                        .setChatId(user.getChatId())
                        .setText("Survey")
                        .setReplyMarkup(keyboardMarkup));
            } catch (Exception ex) {
                ex.printStackTrace();
                sendMessage("BAN FROM: " + user.getName(), chatId, bot);
            }

        }
    }

    private ReplyKeyboard getSurveyKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(i));
            button.setCallbackData("cmd=SurveyCommand rating=" + i);
            row.add(button);
            keyboard.add(row);
        }
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private boolean userContains(User user, List<User> users) {
        for (User user1 : users) {
            if (Objects.equals(user.getChatId(), user1.getChatId())) {
                return true;
            }
        }
        return false;
    }
}
