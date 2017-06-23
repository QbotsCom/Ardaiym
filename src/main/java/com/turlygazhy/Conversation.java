package com.turlygazhy;

import com.turlygazhy.command.Command;
import com.turlygazhy.command.impl.AcceptUserInviteCommand;
import com.turlygazhy.command.impl.ShowInfoCommand;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.ButtonDao;
import com.turlygazhy.dao.impl.KeyboardMarkUpDao;
import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CommandNotFoundException;
import com.turlygazhy.service.CommandService;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
public class Conversation {
    private CommandService commandService = new CommandService();
    private Command command;

    public void handleUpdate(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        String inputtedText;
        if (updateMessage == null) {
            try {
                String data = update.getCallbackQuery().getData();
                inputtedText = data.substring(data.indexOf("cmd=")+4);
            } catch (Exception ex) {
                inputtedText = update.getCallbackQuery().getData();
            }
            updateMessage = update.getCallbackQuery().getMessage();
        } else {
            inputtedText = updateMessage.getText();
        }

        try {
            command = commandService.getCommand(inputtedText);
        } catch (CommandNotFoundException e) {
            if (updateMessage.isGroupMessage()) {
//                if (update.getCallbackQuery().getData() != null) {
//                    String commandString = update.getCallbackQuery().getData();
//                    commandString = commandString.substring(commandString.indexOf("cmd=") + 4);
//                    try {
//                        command = commandService.getCommand(commandString);
//                    } catch (CommandNotFoundException e1) {
//                        e1.printStackTrace();
//                    }
//                }
                return;
            }

        }
        if (command == null) {
            command = new ShowInfoCommand();
            int cannotHandleUpdateMessageId = 7;
            command.setMessageId(cannotHandleUpdateMessageId);
        }


        boolean commandFinished = command.execute(update, bot);
        if (commandFinished) {
            command = null;
        }
    }
}
