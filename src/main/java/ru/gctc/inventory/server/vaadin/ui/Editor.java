package ru.gctc.inventory.server.vaadin.ui;

import com.flowingcode.vaadin.addons.ironicons.IronIcons;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.*;
import ru.gctc.inventory.server.vaadin.providers.InventoryEntityManager;
import ru.gctc.inventory.server.vaadin.utils.PhotoUpload;

import java.util.Map;

@UIScope
@Component
public class Editor extends Dialog {
    public enum Mode { ADD, EDIT }
    private final Map<Class<? extends InventoryEntity>, String> entityTypes = Map.of(
            Building.class, "Здание",
            Floor.class,    "Этаж",
            Room.class,     "Кабинет",
            Container.class,"Шкаф/стеллаж",
            Place.class,    "Полка/позиция",
            Item.class,     "Объект"
    );
    private final Map<Container.Type, String> containerTypes = Map.of(
            Container.Type.CASE, "Шкаф",
            Container.Type.RACK, "Стеллаж"
    );
    private final Map<Place.Type, String> placeTypes = Map.of(
            Place.Type.SHELF,   "Полка",
            Place.Type.POSITION,"Позиция"
    );
    private final Map<Item.Status, String> itemStatus = Map.of(
            Item.Status.IN_USE, "В эксплуатации",
            Item.Status.WRITTEN_OFF, "Списано",
            Item.Status.TRANSFERRED, "Передано на отв. хранение"
    );

    private final FormLayout form;
    private final H2 header;
    private final ComboBox<String> entityType, containerType, placeType,
                                   building, floor, room, container, place;
    private final IntegerField numberField;
    private final TextField nameField, descriptionField;
    private final IntegerField countField;
    private final BigDecimalField costField;
    private final ComboBox<String> itemStatusField;
    private final TextField inventoryNumberField, waybillField, factoryField;
    private final DatePicker incomingDateField, writeoffDateField,
                             sheduledWriteoffDateField, commissioningDateField;
    private final PhotoUpload photoUpload;
    private final Image uploadedPhoto;
    private final MemoryBuffer photoBuffer;
    private final Button confirmButton, cancelButton;
    private final Span interval;

    public Editor() {
        form = new FormLayout();
        add(form);

        /* COMMON */

        header = new H2();

        entityType = new ComboBox<>("Тип");
        entityType.setItems(entityTypes.values());

        /* PATH */

        building = new ComboBox<>("Здание");
        floor = new ComboBox<>("Этаж");
        room = new ComboBox<>("Кабинет");
        container = new ComboBox<>("Шкаф/стеллаж");
        place = new ComboBox<>("Полка/позиция");

        /* CONTAINER */

        containerType = new ComboBox<>("Шкаф/стеллаж");
        entityType.setItems(containerTypes.values());

        /* PLACE */

        placeType = new ComboBox<>("Полка/позиция");
        entityType.setItems(placeTypes.values());

        /* FLOOR, ROOM, CONTAINER, PLACE */

        numberField = new IntegerField("Номер");
        numberField.setHasControls(true);

        /* ROOM, CONTAINER, PLACE */

        nameField = new TextField("Наименование");
        nameField.setClearButtonVisible(true);

        /* ITEM */

        descriptionField = new TextField("Описание");
        descriptionField.setClearButtonVisible(true);

        countField = new IntegerField("Количество");
        countField.setMin(1);
        countField.setHasControls(true);

        costField = new BigDecimalField("Стоимость, ₽");
        costField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        itemStatusField = new ComboBox<>("Статус объекта");
        itemStatusField.setItems(itemStatus.values());

        inventoryNumberField = new TextField("Инвентарный номер");
        inventoryNumberField.setClearButtonVisible(true);

        waybillField = new TextField("Номер накладной получения");
        waybillField.setClearButtonVisible(true);

        factoryField = new TextField("Заводской номер изделия");
        factoryField.setClearButtonVisible(true);

        incomingDateField = new DatePicker("Дата получения");
        incomingDateField.setClearButtonVisible(true);

        writeoffDateField = new DatePicker("Дата списания");
        writeoffDateField.setClearButtonVisible(true);

        sheduledWriteoffDateField = new DatePicker("Дата планового списания");
        sheduledWriteoffDateField.setClearButtonVisible(true);

        commissioningDateField = new DatePicker("Ввод в эксплуатацию");
        commissioningDateField.setClearButtonVisible(true);

        photoBuffer = new MemoryBuffer();
        photoUpload = new PhotoUpload(photoBuffer);
        VerticalLayout uploadLabelsLayout = new VerticalLayout(
                new Label("Загрузите фото внешнего вида."),
                new Label("Файл формата *.jpeg, *.png или *.gif размером не более 20МБ.")
        );
        uploadLabelsLayout.setSpacing(false);
        photoUpload.setDropLabel(uploadLabelsLayout);
        uploadedPhoto = new Image();
        uploadedPhoto.setMaxHeight("20%");
        uploadedPhoto.setMaxWidth("20%");
        photoUpload.addSucceededListener(event -> {
            StreamResource photo = new StreamResource(event.getFileName(), photoBuffer::getInputStream);
            uploadedPhoto.setSrc(photo);
        });
        photoUpload.addPhotoRemoveListener(event -> uploadedPhoto.setSrc(""));
        photoUpload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        photoUpload.setMaxFiles(1);
        photoUpload.setMaxFileSize(1024*1024*20);

        /* COMMON */

        confirmButton = new Button();
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton = new Button("Отмена");
        interval = new Span();
    }

    public void show(Mode mode, InventoryEntityManager<? extends InventoryEntity> entityManager) {
        form.removeAll();
        switch (mode) {
            case ADD -> {
                header.setText("Добавить");
                confirmButton.setText("Добавить");
                confirmButton.setIcon(VaadinIcon.PLUS.create());
            }
            case EDIT -> {
                header.setText("Редактировать");
                confirmButton.setText("Сохранить");
                confirmButton.setIcon(IronIcons.SAVE.create());
            }
        }
        form.add(header);
        InventoryEntity entity = entityManager.getInventoryEntity();
        if(entity instanceof Building) {
            form.add(nameField);
        }
        else if(entity instanceof Floor) {
            form.add(building);
            form.add(numberField);
        }
        else if(entity instanceof Room) {
            form.add(building, floor);
            form.add(numberField, nameField);
        }
        else if(entity instanceof Container) {
            form.add(building, floor, room);
            form.add(numberField, nameField);
        }
        else if(entity instanceof Place) {
            form.add(building, floor, room, container);
            form.add(numberField, nameField);
        }
        else {
            form.add(building, floor, room, container, place);
            form.add(nameField,descriptionField,countField,costField,itemStatusField,
                    inventoryNumberField,waybillField,factoryField,
                    incomingDateField,writeoffDateField,sheduledWriteoffDateField,commissioningDateField,
                    photoUpload,uploadedPhoto);
            form.add(interval, confirmButton, cancelButton);

            form.setResponsiveSteps(new FormLayout.ResponsiveStep("0px",5));
            form.setColspan(header,                 5);
            form.setColspan(nameField,              2);
            form.setColspan(descriptionField,       3);
            form.setColspan(costField,              2);
            form.setColspan(itemStatusField,        2);
            form.setColspan(inventoryNumberField,   2);
            form.setColspan(waybillField,           2);
            form.setColspan(incomingDateField,      2);
            form.setColspan(photoUpload,            3);
            form.setColspan(uploadedPhoto,          1);
            form.setColspan(interval,               3);
        }
        // ...
        open();
    }
}
