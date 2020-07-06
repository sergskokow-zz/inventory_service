package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.Item;

@UIScope
@Component
public class Viewer extends Dialog {

    private final FormLayout form;
    private final H3 name;
    private final Image photo;
    private final TextArea description;
    private final TextField status;
    private final IntegerField count;
    private final BigDecimalField cost;
    private final TextField number, waybill, factory;
    private final DatePicker inventory, incoming, commissioning, writeoff, sheduled_writeoff;

    public Viewer() {
        form = new FormLayout();
        add(form);

        HorizontalLayout mainLayout = new HorizontalLayout();
        form.add(mainLayout);

        photo = new Image(); // TODO style
        VerticalLayout dataLayout = new VerticalLayout();
        mainLayout.addAndExpand(photo);
        mainLayout.add(dataLayout);

        name = new H3();
        description = new TextArea("Описание");
        status = new TextField("Статус");

        number = new TextField("Инвентарный номер");
        waybill = new TextField("Номер накладной получения");
        factory = new TextField();
        count = new IntegerField();
        cost = new BigDecimalField();

        inventory = new DatePicker();
        incoming = new DatePicker();
        commissioning = new DatePicker();
        writeoff = new DatePicker();
        sheduled_writeoff = new DatePicker();
    }

    public void show(Item item) {

    }
}
