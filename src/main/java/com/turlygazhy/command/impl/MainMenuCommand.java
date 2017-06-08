package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.AboutMessage;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.News;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lol on 02.06.2017.
 */

public class MainMenuCommand extends Command {

    protected MainMenuCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            sendMessage(5, chatId, bot);        // Главное меню
            waitingType = WaitingType.COMMAND;
            return false;
        }

        switch (waitingType) {
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(50))) {     // О нас
                    AboutMessage aboutMessage = aboutMessageDao.getAbout();
                    sendMessage(aboutMessage.getText(), chatId, bot);
                    if (aboutMessage.getPhoto() != null) {
                        bot.sendPhoto(new SendPhoto()
                                .setPhoto(aboutMessage.getPhoto())
                                .setChatId(chatId));
                    }
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(51))) {     // Контакты
                    AboutMessage aboutMessage = aboutMessageDao.getContacts();
                    sendMessage(aboutMessage.getText(), chatId, bot);
                    if (aboutMessage.getPhoto() != null) {
                        bot.sendPhoto(new SendPhoto()
                                .setPhoto(aboutMessage.getPhoto())
                                .setChatId(chatId));
                    }
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(52))) {     // Личный кабинет
                    sendMessage("Personal Area", chatId, bot);
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(53))) {     // Группа
                    AboutMessage aboutMessage = aboutMessageDao.getGroup();
                    sendMessage(aboutMessage.getText(), chatId, bot);
                    if (aboutMessage.getPhoto() != null) {
                        bot.sendPhoto(new SendPhoto()
                                .setPhoto(aboutMessage.getPhoto())
                                .setChatId(chatId));
                    }
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(54))) {     // Планы на год
                    AboutMessage aboutMessage = aboutMessageDao.getPlansForYear();
                    sendMessage(aboutMessage.getText(), chatId, bot);
                    if (aboutMessage.getPhoto() != null) {
                        bot.sendPhoto(new SendPhoto()
                                .setPhoto(aboutMessage.getPhoto())
                                .setChatId(chatId));
                    }
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(55))) {     // Новости
                    List<News> news = newsDao.getNews();
                    if (news == null) {
                        sendMessage("No news yet", chatId, bot);
                        return false;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (News newsObj : news) {
                        sb.append("/id").append(newsObj.getId()).append(" - ").append(newsObj.getTitle()).append("\n");
                    }
                    sendMessage(70, chatId, bot);
                    sendMessage(sb.toString(), chatId, bot);
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }
                return false;

            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(5, chatId, bot);
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                int id = Integer.parseInt(updateMessageText.substring(3));
                News newsObj = newsDao.getNews(id);
                bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(newsObj.toString())
                        .setParseMode(ParseMode.HTML)
                        .setReplyMarkup(keyboardMarkUpDao.select(5)));

                if (newsObj.getPhoto() != null) {
                    String[] photos = newsObj.getPhoto().split(";");
                    for (String photo : photos)
                        bot.sendPhoto(new SendPhoto()
                                .setChatId(chatId)
                                .setPhoto(photo));
                }
                waitingType = WaitingType.COMMAND;
                return false;
        }

        return false;
    }
}
