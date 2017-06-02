package com.turlygazhy.command;

import com.turlygazhy.command.impl.*;
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
            default:
                throw new NotRealizedMethodException("Not realized for type: " + type);
        }
    }
}
