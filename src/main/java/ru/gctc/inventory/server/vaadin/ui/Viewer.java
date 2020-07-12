package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import de.codecamp.vaadin.components.messagedialog.MessageDialog;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.barcodes.Barcode;
import org.vaadin.olli.ClipboardHelper;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.services.ItemService;
import ru.gctc.inventory.server.db.utils.ItemPath;
import ru.gctc.inventory.server.vaadin.utils.DateCast;
import ru.gctc.inventory.server.vaadin.utils.InventoryEntityNames;
import ru.gctc.inventory.server.vaadin.utils.LinkFactory;
import ru.gctc.inventory.server.vaadin.utils.PhotoViewer;

@UIScope
@Component
@Route(value = "item", layout = MainView.class)
@CssImport("./my-styles/viewer.css")
public class Viewer extends Dialog implements HasUrlParameter<Long>, BeforeEnterObserver {

    private final H3 name;
    private final HorizontalLayout qrLayout;
    private final TextField link;

    private final Binder<Item> binder;

    public Viewer() {
        /* layouts */
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setPadding(false);
        add(mainLayout);

        PhotoViewer photo = new PhotoViewer();
        photo.setAlt("Фото отсутствует");
        photo.setMaxHeight("100%");
        photo.setMaxWidth("100%");
        VerticalLayout photoLayout = new VerticalLayout();
        photoLayout.setId("photo_layout");
        VerticalLayout dataLayout = new VerticalLayout();
        mainLayout.addAndExpand(photoLayout, dataLayout);
        photoLayout.add(photo);
        photoLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0px",3));
        dataLayout.addAndExpand(form);

        /* controls */

        name = new H3();
        Button closeButton = new Button("Закрыть", VaadinIcon.CLOSE.create());
        closeButton.addClickListener(event -> close());
        TextArea path = new TextArea("Местонахождение");
        link = new TextField("Ссылка");
        link.setAutoselect(true);
        ClipboardHelper linkCopyButton = new ClipboardHelper();
        linkCopyButton.wrap(new Button("Копировать", VaadinIcon.COPY.create()));
        qrLayout = new HorizontalLayout();
        qrLayout.setId("qr_code");
        qrLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        link.addValueChangeListener(event -> linkCopyButton.setContent(event.getValue()));

        TextArea description = new TextArea("Описание");
        TextField status = new TextField("Статус");
        IntegerField count = new IntegerField("Количество");
        BigDecimalField cost = new BigDecimalField("Стоимость, ₽");

        TextField number = new TextField("Инвентарный номер");
        TextField waybill = new TextField("Номер накладной получения");
        TextField factory = new TextField("Заводской номер");

        DatePicker inventory = new DatePicker("Добавлено");
        DatePicker incoming = new DatePicker("Дата получения");
        DatePicker commissioning = new DatePicker("Ввод в эксплуатацию");
        DatePicker writeoff = new DatePicker("Дата списания");
        DatePicker sheduled_writeoff = new DatePicker("Дата планового списания");

        form.add(name, 2);
        form.add(closeButton);
        form.add(path, 3);
        form.add(qrLayout, link, linkCopyButton, description, status, count, cost,
                number, waybill, factory,
                inventory, incoming, commissioning, writeoff, sheduled_writeoff);
        form
                .getChildren()
                .filter(component -> component instanceof HasValue)
                .map(component -> (HasValue<?,?>)component)
                .forEach(hasValue -> hasValue.setReadOnly(true));

        /* binding */

        binder = new Binder<>();
        binder.setReadOnly(true);
        binder.bind(path, ItemPath::toString, null);
        binder.bind(link, item -> LinkFactory.get(item.getId()), null);
        binder.bind(description, Item::getDescription, null);
        binder.bind(status, item -> InventoryEntityNames.itemStatus.get(item.getStatus()), null);
        binder.bind(count, Item::getCount, null);
        binder.bind(cost, Item::getCost, null);
        binder.bind(number, Item::getNumber, null);
        binder.bind(waybill, Item::getWaybill, null);
        binder.bind(factory, Item::getFactory, null);
        binder.bind(inventory, item -> DateCast.toLocalDate(item.getInventory()), null);
        binder.bind(incoming, item -> DateCast.toLocalDate(item.getIncoming()), null);
        binder.bind(commissioning, item -> DateCast.toLocalDate(item.getCommissioning()), null);
        binder.bind(writeoff, item -> DateCast.toLocalDate(item.getWriteoff()), null);
        binder.bind(sheduled_writeoff, item -> DateCast.toLocalDate(item.getSheduled_writeoff()), null);
        binder.bind(photo, Item::getPhoto, null);
    }

    @Setter
    private Item target;

    @Override
    public void open() {
        if(target!=null) {
            name.setText(target.getName());
            binder.readBean(target);
            qrLayout.removeAll();
            qrLayout.add(new Barcode(link.getValue()));
            super.open();
        }
    }

    /* links support */

    private ItemService itemService;
    @Autowired
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        target = itemService.getById(parameter).orElse(null);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(target==null)
            event.rerouteTo(NotFoundMessageDialog.class);
        else
            open();
    }
}

@Route(value = "item/notfound", layout = MainView.class)
@UIScope
class NotFoundMessageDialog extends MessageDialog {
    public NotFoundMessageDialog() {
        super();
        setTitle("Не найдено", VaadinIcon.BAN.create());
        setMessage("Объект не найден по указанной ссылке.");
        addButton().text("ОК").primary().closeOnClick();
        open();
    }
}