package ru.gctc.inventory.server.vaadin.utils;

import java.net.InetAddress;

public abstract class LinkFactory {
    public static String get(Long id) {
        return String.format("%s/item/%d", InetAddress.getLoopbackAddress().getHostName(), id);
    }
}
