package ru.gctc.inventory.server.vaadin.utils;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

public class PhotoUpload extends Upload {
    public PhotoUpload(MemoryBuffer photoBuffer) {
        super(photoBuffer);
    }

    public void addPhotoRemoveListener(ComponentEventListener<PhotoRemoveEvent> listener) {
        super.addListener(PhotoRemoveEvent.class, listener);
    }
}
