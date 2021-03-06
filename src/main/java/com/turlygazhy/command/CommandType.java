package com.turlygazhy.command;

import com.turlygazhy.exception.NotRealizedMethodException;

/**
 * Created by user on 1/1/17.
 */
public enum CommandType {
    SHOW_INFO(1),
    ADMIN_MENU(2),
    ADD_TO_PARTICIPANT_OD_STOCK(3),
    ACCEPT_INVITE(4),
    EDIT_DESCRIPTION(5),
    EDIT_NEWS(6),
    ADD_ADMIN(14),
    ADMIN_NEW_STOCK(20),
    ADMIN_NEW_DISTRIBUTION(21),
    SHOW_STOCK(22),
    SURVEY(30),
    NEW_SURVEY(31),
    SURVEY_MENU(32);


    private final int id;

    CommandType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CommandType getType(long id) {
        for (CommandType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new NotRealizedMethodException("There are no type for id: " + id);
    }
}
