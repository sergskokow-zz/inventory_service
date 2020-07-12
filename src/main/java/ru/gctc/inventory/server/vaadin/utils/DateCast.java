package ru.gctc.inventory.server.vaadin.utils;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public abstract class DateCast {
    @Getter @Setter
    private static ZoneId defaultTimeZone = ZoneId.of("Europe/Moscow");// TODO client timezone

    public static Date toDate(LocalDate date) {
        if(date==null)
            return null;
        return java.sql.Date.valueOf(date);
    }
    public static LocalDate toLocalDate(Date date) {
        if(date==null)
            return null;
        return LocalDate.ofInstant(new Date(date.getTime()).toInstant(), defaultTimeZone);
    }
}
