package ru.gctc.inventory.server.vaadin.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateCast {
    public static Date toDate(LocalDate date) {
        if(date==null)
            return null;
        return java.sql.Date.valueOf(date);
    }
    public static LocalDate toLocalDate(Date date) {
        if(date==null)
            return null;
        return LocalDate.ofInstant(new Date(date.getTime()).toInstant(), ZoneId.systemDefault());
    }
}
