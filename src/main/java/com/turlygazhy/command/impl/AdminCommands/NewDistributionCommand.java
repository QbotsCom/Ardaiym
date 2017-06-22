package com.turlygazhy.command.impl.AdminCommands;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.ParticipantOfStock;
import com.turlygazhy.entity.Stock;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by lol on 05.06.2017.
 */
public class NewDistributionCommand extends Command {
    private List<User> users;
    private Stock stock;

    public NewDistributionCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            sendMessage(41, chatId, bot);   // Для кого рассылка
            waitingType = WaitingType.FOR_WHOM;
            return false;
        }

        switch (waitingType) {

            ///////// Выбираем каких волонтерам пересылать сообщения ////////////

            case FOR_WHOM:
                if (updateMessageText.equals(buttonDao.getButtonText(80))) { // Для всех
                    sendMessage(40, chatId, bot);
                    users = userDao.getUsers();
                    waitingType = WaitingType.MESSAGE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(81))) { // Для волонтеров акции
                    bot.sendMessage(new SendMessage()
                            .setChatId(chatId)
                            .setText(messageDao.getMessageText(42))
                            .setReplyMarkup(getChooseStockKeyboard(stockDao.getUndoneStocks())));
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                return false;
            case CHOOSE:
                int stockId = Integer.parseInt(updateMessageText);
                stock = stockDao.getStock(stockId);
                bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(43))
                        .setReplyMarkup(getChooseWorkKeyboard(stock.getTypeOfWork())));
                waitingType = WaitingType.CHOOSE_TYPE_OF_WORK;
                return false;

            case CHOOSE_TYPE_OF_WORK:
                if (updateMessageText.equals(buttonDao.getButtonText(80))) {
                    users = new ArrayList<>();
                    for (ParticipantOfStock participantOfStock : stock.getParticipantOfStocks()) {
                        if (!userContains(participantOfStock.getUser())) {
                            users.add(participantOfStock.getUser());
                        }
                    }
                } else {
                    List<ParticipantOfStock> participantOfStocks = stock.getParticipantOfStocks();
                    if (participantOfStocks.size() == 0) {
                        sendMessage("NO VOLUNTEERS", chatId, bot);
                        return false;
                    }
                    users = new ArrayList<>();
                    for (ParticipantOfStock participantOfStock : participantOfStocks) {
                        if (participantOfStock.getTypeOfWork().equals(updateMessageText)) {
                            users.add(participantOfStock.getUser());
                        }
                    }
                }
                sendMessage(40, chatId, bot);
                waitingType = WaitingType.MESSAGE;
                return false;

            ///////// Сама рассылка сообщений /////////

            case MESSAGE:
                sendMessage(88, chatId, bot);

                for (User user : users) {
                    try {
                        bot.sendMessage(new SendMessage()
                                .setText(messageDao.getMessageText(44) + "\n<b>" + stock.getName() + "</b>\n" + updateMessageText)
                                .setChatId(user.getChatId())
                                .setParseMode(ParseMode.HTML));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        sendMessage("BAN FROM: " + user.getName(), chatId, bot);
                    }
                }
                sendMessage(49, chatId, bot);
                return true;
        }

        return false;
    }

    private boolean userContains(User user) {
        for (User user1 : users) {
            if (Objects.equals(user.getChatId(), user1.getChatId())) {
                return true;
            }
        }
        return false;
    }

    private ReplyKeyboard getChooseWorkKeyboard(List<String> typesOfWork) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (String typeOfWork : typesOfWork) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(typeOfWork);
            button.setCallbackData(typeOfWork);
            buttons.add(button);
            row.add(buttons);
        }
        InlineKeyboardButton forAll = new InlineKeyboardButton();
        forAll.setText(buttonDao.getButtonText(80));
        forAll.setCallbackData(buttonDao.getButtonText(80));
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(forAll);
        row.add(buttons);
        keyboard.setKeyboard(row);
        return keyboard;
    }

    private ReplyKeyboard getChooseStockKeyboard(List<Stock> undoneStocks) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();
        for (Stock stock : undoneStocks) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(stock.getName());
            button.setCallbackData(String.valueOf(stock.getId()));
            buttons.add(button);
            row.add(buttons);
        }
        keyboard.setKeyboard(row);
        return keyboard;
    }
}
