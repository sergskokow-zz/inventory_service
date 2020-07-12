package ru.gctc.inventory.server.vaadin.utils;

public abstract class LinkFactory {
    private static final String hostName = System.getenv("APP_ADDRESS");

    public static String get(Long id) {
        return String.format("%s/item/%d", hostName, id);
    }
}