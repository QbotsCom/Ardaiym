package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.News;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by lol on 08.06.2017.
 */
public class NewsMenuCommand extends Command {
    News news;

    public NewsMenuCommand() throws SQLException {
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);

        if (waitingType == null) {
            sendMessage(91, chatId, bot);       // Меню новостей
            waitingType = WaitingType.COMMAND;
            return false;
        }

        switch (waitingType) {
            case COMMAND:
                if (updateMessageText.equals(buttonDao.getButtonText(70))) {    // Добавить новость
                    sendMessage(100, chatId, bot);      // Введите новость
                    waitingType = WaitingType.TITLE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(71))) {    // Редактировать новость
                    sendMessage(109, chatId, bot);      // Выберите новость
                    List<News> news = newsDao.getNews();
                    StringBuilder sb = new StringBuilder();
                    for (News newsObj : news) {
                        sb.append("/id").append(newsObj.getId()).append(" - ").append(newsObj.getTitle()).append("\n");
                    }
                    sendMessage(sb.toString(), chatId, bot);
                    waitingType = WaitingType.CHOOSE;
                    return false;
                }

                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(6, chatId, bot);
                    return true;
                }
                return false;

            ///////////////////// Добавляем новую новость ///////////////////

            case TITLE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(91, chatId, bot);       // Меню новостей
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                news = new News();
                news.setTitle(updateMessageText);
                sendMessage(101, chatId, bot);
                waitingType = WaitingType.TEXT;
                return false;

            case TEXT:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(100, chatId, bot);      // Введите заголовок
                    waitingType = WaitingType.TITLE;
                    return false;
                }
                news.setText(updateMessageText);
                sendMessage(102, chatId, bot);      // Отправьте фото
                waitingType = WaitingType.PHOTO;
                return false;

            case PHOTO:
                if (updateMessageText != null) {
                    if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                        sendMessage(101, chatId, bot);
                        waitingType = WaitingType.TEXT;
                        return false;
                    }
                    if (updateMessageText.equals(buttonDao.getButtonText(12))) {    // Готово
                        Date date = new Date();
                        String dateString = "";
                        if (date.getDate() <= 9) {
                            dateString = "0";
                        }
                        dateString += date.getDate() + ".";
                        if (date.getMonth() + 1 <= 9) {
                            dateString += "0";
                        }
                        dateString += date.getMonth() + 1;
                        news.setDate(dateString);
                        newsDao.addNews(news);
                        sendMessage(103, chatId, bot);
                        waitingType = WaitingType.COMMAND;
                        return false;
                    }
                }
                if (updateMessage.getPhoto() != null) {
                    List<PhotoSize> photos = update.getMessage().getPhoto();
                    for (int i = 3; i< photos.size(); i+=4) {
                        PhotoSize photo = photos.get(i);
                        news.addPhoto(photo.getFileId());
                    }
                    return false;
                }
                return false;

            ////////////////// Редактируем уже добавленные новости ////////////////////////////

            case CHOOSE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    sendMessage(91, chatId, bot);
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                news = newsDao.getNews(Integer.valueOf(updateMessageText.substring(3)));
                bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(news.toString())
                        .setParseMode(ParseMode.HTML));
                sendMessage(104, chatId, bot);      // Что будем изменять?
                waitingType = WaitingType.EDIT_NEWS;
                return false;

            case EDIT_NEWS:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(91, chatId, bot);       // Меню новостей
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(73))) {    // Изменить заголовок
                    sendMessage(100, chatId, bot);      // Введите заголовок
                    waitingType = WaitingType.EDIT_NEWS_TITLE;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(74))) {    // Изменить текст
                    sendMessage(101, chatId, bot);
                    waitingType = WaitingType.EDIT_NEWS_TEXT;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(75))) {    // Добавить/удалить фото
                    if (news.getPhoto() == null){
                        sendMessage("A PHOTO ZHOK", chatId, bot);
                        return false;
                    }
                    String[] photos = news.getPhoto().split(";");
                    for (int i = 0; i < photos.length; i++) {
                        String photo = photos[i];
                        bot.sendPhoto(new SendPhoto()
                                .setChatId(chatId)
                                .setPhoto(photo));
                    }
                    sendMessage(105, chatId, bot);      // Отправь новый фото или перешли старые, чтобы удалить их
                    waitingType = WaitingType.EDIT_NEWS_PHOTO;
                    return false;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(76))) {    // Показывать в ленте
                    if (news.isShow()) {
                        news.setShow(false);
                        newsDao.updateNews(news);
                        sendMessage(107, chatId, bot);      // Новость больше не показывается в ленте
                    } else {
                        news.setShow(true);
                        newsDao.updateNews(news);
                        sendMessage(108, chatId, bot);      // Теперь новость видно в ленте
                    }
                    return false;
                }
                return false;

            case EDIT_NEWS_TITLE:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(91, chatId, bot);       // Меню новостей
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                news.setTitle(updateMessageText);
                newsDao.updateNews(news);
                sendMessage(104, chatId, bot);      // Что будем изменять?
                waitingType = WaitingType.EDIT_NEWS;
                return false;

            case EDIT_NEWS_TEXT:
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                    sendMessage(91, chatId, bot);       // Меню новостей
                    waitingType = WaitingType.COMMAND;
                    return false;
                }
                news.setText(updateMessageText);
                newsDao.updateNews(news);
                sendMessage(104, chatId, bot);      // Что будем изменять?
                waitingType = WaitingType.EDIT_NEWS;
                return false;

            case EDIT_NEWS_PHOTO:
                if (updateMessageText != null) {
                    if (updateMessageText.equals(buttonDao.getButtonText(10))) {    // Назад
                        sendMessage(91, chatId, bot);       // Меню новостей
                        waitingType = WaitingType.COMMAND;
                        return false;
                    }
                    if (updateMessageText.equals(buttonDao.getButtonText(12))) {    // Готово
                        sendMessage(106, chatId, bot);      // Готово
                        waitingType = WaitingType.EDIT_NEWS;
                        return false;
                    }
                }
                if (updateMessage.getForwardFrom() != null) {
                    String photos = news.getPhoto();
                    news.setPhoto(photos.replaceFirst(updateMessage.getPhoto().get(3).getFileId()+";", ""));
                    newsDao.updateNews(news);
                    sendMessage("Deleted", chatId, bot);
                    return false;
                }
                if (updateMessage.hasPhoto()) {
                    String photos = news.getPhoto();
                    news.setPhoto(photos.concat(updateMessageText) + ";");
                    newsDao.updateNews(news);
                    sendMessage("Added", chatId, bot);
                }
                return false;
        }

        return false;
    }
}
