package ru.gctc.inventory.server.vaadin.utils;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import ru.gctc.inventory.server.db.entities.Photo;

import java.sql.SQLException;

public class PhotoViewer extends Image implements HasValue<PhotoViewer.PhotoSourceChangeEvent, Photo> {

    interface PhotoSourceChangeEvent extends HasValue.ValueChangeEvent<Photo> {}

    @Override
    public void setValue(Photo photo) {
        if(photo!=null) {
            StreamResource resource = new StreamResource("photo", () -> {
                try {
                    return photo.getData().getBinaryStream();
                } catch (SQLException ignored) { return null; }
            });
            resource.setContentType(photo.getMime());
            setSrc(resource);
        }
        else
            setSrc("");
    }

    @Override
    public Photo getValue() {
        return null;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super PhotoSourceChangeEvent> listener) {
        return null;
    }

    @Override
    public void setReadOnly(boolean readOnly) {

    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {

    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }

    public void clear() {
        setValue(null);
    }

    public boolean isEmpty() {
        return getSrc()==null || getSrc().isEmpty();
    }
}

