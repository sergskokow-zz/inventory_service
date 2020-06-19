package ru.gctc.inventory.server.db.services.exceptions;

public abstract class ServiceException extends Exception {
    public ServiceException(String s) {
        super(s);
    }
}