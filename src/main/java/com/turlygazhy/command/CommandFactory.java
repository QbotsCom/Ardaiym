package com.turlygazhy.command;

import com.turlygazhy.command.impl.*;
import com.turlygazhy.command.impl.AdminCommands.*;
import com.turlygazhy.exception.NotRealizedMethodException;

import java.sql.SQLException;

/**
 * Created by user on 1/2/17.
 */
public class CommandFactory {
    public static Command getCommand(long id) throws SQLException {
        CommandType type = CommandType.getType(id);
        switch (type) {
            case SHOW_INFO:
                return new ShowInfoCommand();
            case ADMIN_MENU:
                return new AdminMenuCommand();
            case ADD_TO_PARTICIPANT_OD_STOCK:
                return new AddToParticipantOfStock();
            case ACCEPT_INVITE:
                return new AcceptUserInviteCommand();
            case ADMIN_NEW_DISTRIBUTION:
                return new NewDistributionCommand();
            case ADMIN_NEW_STOCK:
                return new NewStockCommand();
            case SHOW_STOCK:
                return new ShowStocksCommand();
            case EDIT_DESCRIPTION:
                return new EditDescriptionCommand();
            case EDIT_NEWS:
                return new NewsMenuCommand();
            case ADD_ADMIN:
                return new AddAdminCommand();
            case SURVEY:
                return new SurveyCommand();
            case NEW_SURVEY:
                return new NewSurveyCommand();
            case SURVEY_MENU:
                return new SurveyMenuCommand();
            default:
                throw new NotRealizedMethodException("Not realized for type: " + type);
        }
    }
}
