package ru.gctc.inventory.server.vaadin.utils;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.upload.Upload;

@DomEvent("file-remove")
public class PhotoRemoveEvent extends ComponentEvent<Upload> {
    public PhotoRemoveEvent(Upload source, boolean fromClient) {
        super(source, fromClient);
    }
}
