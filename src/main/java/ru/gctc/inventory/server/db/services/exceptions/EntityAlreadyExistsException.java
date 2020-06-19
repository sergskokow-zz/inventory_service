package ru.gctc.inventory.server.db.services.exceptions;

public class EntityAlreadyExistsException extends ServiceException {
    public EntityAlreadyExistsException(String type, long id, String s) {
        super(String.format("Entity %s #%d \"%s\" already exists.", type, id, s));
    }
}