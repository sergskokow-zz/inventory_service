package ru.gctc.inventory.server.vaadin.ui;

import com.flowingcode.vaadin.addons.ironicons.IronIcons;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import de.codecamp.vaadin.components.messagedialog.MessageDialog;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.*;
import ru.gctc.inventory.server.db.services.*;
import ru.gctc.inventory.server.db.services.exceptions.EntityNotFoundException;
import ru.gctc.inventory.server.vaadin.exceptions.FloatingItemException;
import ru.gctc.inventory.server.vaadin.exceptions.RequiredFieldNotFilledException;
import ru.gctc.inventory.server.vaadin.utils.DateCast;
import ru.gctc.inventory.server.vaadin.utils.InventoryEntityNames;
import ru.gctc.inventory.server.vaadin.utils.PhotoUpload;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@UIScope
@Component
public class Editor extends Dialog {

    private BuildingService buildingService;
    private FloorService floorService;
    private RoomService roomService;
    private ContainerService containerService;
    private PlaceService placeService;
    private ItemService itemService;
    @Autowired
    protected void setItemService(BuildingService buildingService,
                                  FloorService floorService,
                                  RoomService roomService,
                                  ContainerService containerService,
                                  PlaceService placeService,
                                  ItemService itemService) {
        this.buildingService = buildingService;
        this.floorService = floorService;
        this.roomService = roomService;
        this.containerService = containerService;
        this.placeService = placeService;
        this.itemService = itemService;
    }

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
    private final Map<Integer, FormLayout.ResponsiveStep> columns = Map.of(
            1, new FormLayout.ResponsiveStep("0px",1),
            2, new FormLayout.ResponsiveStep("0px",2),
            3, new FormLayout.ResponsiveStep("0px",3),
            4, new FormLayout.ResponsiveStep("0px",4),
            5, new FormLayout.ResponsiveStep("0px",5)
    );

    private final FormLayout form;
    private final H2 header;
    private final ComboBox<Container.Type> containerType;
    private final ComboBox<Place.Type> placeType;
    private final ComboBox<Building> buildingField;
    private final ComboBox<Floor> floorField;
    private final ComboBox<Room> roomField;
    private final ComboBox<Container> containerField;
    private final ComboBox<Place> placeField;
    private final IntegerField numberField;
    private final TextField nameField;
    private final TextArea descriptionField;
    private final IntegerField countField;
    private final BigDecimalField costField;
    private final ComboBox<Item.Status> itemStatusField;
    private final TextField inventoryNumberField, waybillField, factoryField;
    private final DatePicker incomingDateField, writeoffDateField,
                             sheduledWriteoffDateField, commissioningDateField;
    private final PhotoUpload photoUpload;
    private final Image uploadedPhoto;
    private final MemoryBuffer photoBuffer;
    private final Button confirmButton, cancelButton;
    private final Span interval;

    private final Notification notification;

    public Editor() {
        form = new FormLayout();
        add(form);

        /* COMMON */

        header = new H2();

        /* PATH */

        buildingField = new ComboBox<>("Здание");
        buildingField.setItemLabelGenerator(InventoryEntityNames::get);
        buildingField.setAllowCustomValue(false);
        buildingField.setRequired(true);

        floorField = new ComboBox<>("Этаж");
        floorField.setItemLabelGenerator(InventoryEntityNames::get);
        floorField.setAllowCustomValue(false);
        floorField.setRequired(true);

        roomField = new ComboBox<>("Кабинет");
        roomField.setItemLabelGenerator(InventoryEntityNames::get);
        roomField.setAllowCustomValue(false);
        roomField.setRequired(true);

        containerField = new ComboBox<>("Шкаф/стеллаж");
        containerField.setItemLabelGenerator(InventoryEntityNames::get);
        containerField.setAllowCustomValue(false);

        placeField = new ComboBox<>("Полка/позиция");
        placeField.setItemLabelGenerator(InventoryEntityNames::get);
        placeField.setAllowCustomValue(false);

        buildingField.setDataProvider(
                (filter, offset, limit) -> buildingService.getAll(offset,limit).stream(),
                s -> (int) buildingService.count());
        buildingField.addValueChangeListener(event -> {
            if (event.getHasValue().isEmpty())
                floorField.setItems(List.of());
            else {
                floorField.setDataProvider(
                        (filter, offset, limit) -> floorService.getChildren(event.getValue(),offset,limit).stream(),
                        s -> (int) floorService.getChildCount(event.getValue()));
            }
        });
        floorField.addValueChangeListener(event -> {
            if (event.getHasValue().isEmpty())
                roomField.setItems(List.of());
            else {
                roomField.setDataProvider(
                        (filter, offset, limit) -> roomService.getChildren(event.getValue(),offset,limit).stream(),
                        s -> (int) roomService.getChildCount(event.getValue()));
            }
        });
        roomField.addValueChangeListener(event -> {
            if (event.getHasValue().isEmpty())
                containerField.setItems(List.of());
            else {
                containerField.setDataProvider(
                        (filter, offset, limit) -> containerService.getChildren(event.getValue(),offset,limit).stream(),
                        s -> (int) containerService.getChildCount(event.getValue()));
            }
        });
        containerField.addValueChangeListener(event -> {
            if (event.getHasValue().isEmpty())
                placeField.setItems(List.of());
            else {
                placeField.setDataProvider(
                        (filter, offset, limit) -> placeService.getChildren(event.getValue(),offset,limit).stream(),
                        s -> (int) placeService.getChildCount(event.getValue()));
            }
        });

        /* CONTAINER */

        containerType = new ComboBox<>("Шкаф/стеллаж");
        containerType.setItemLabelGenerator(containerTypes::get);
        containerType.setItems(Container.Type.values());
        containerType.setValue(Container.Type.CASE);
        containerType.setRequired(true);

        /* PLACE */

        placeType = new ComboBox<>("Полка/позиция");
        placeType.setItemLabelGenerator(placeTypes::get);
        placeType.setItems(Place.Type.values());
        placeType.setValue(Place.Type.SHELF);
        placeType.setRequired(true);

        /* FLOOR, ROOM, CONTAINER, PLACE */

        numberField = new IntegerField("Номер");
        numberField.setHasControls(true);
        numberField.setRequiredIndicatorVisible(true);

        /* ROOM, CONTAINER, PLACE */

        nameField = new TextField("Наименование");
        nameField.setClearButtonVisible(true);

        /* ITEM */

        descriptionField = new TextArea("Описание");
        descriptionField.setClearButtonVisible(true);

        countField = new IntegerField("Количество");
        countField.setMin(1);
        countField.setValue(1);
        countField.setHasControls(true);
        countField.setRequiredIndicatorVisible(true);

        costField = new BigDecimalField("Стоимость, ₽");
        costField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        itemStatusField = new ComboBox<>("Статус объекта");
        itemStatusField.setItemLabelGenerator(itemStatus::get);
        itemStatusField.setItems(Item.Status.values());
        itemStatusField.setValue(Item.Status.IN_USE);
        itemStatusField.setRequired(true);

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
        cancelButton.addClickListener(event -> close());
        interval = new Span();

        notification = new Notification();
        notification.setDuration(3000);
    }

    // TODO recursive func
    private void preparePathBoxes(InventoryEntity entity) {
        if(entity==null)
            return;
        Building b = null;
        Floor f = null;
        Room r = null;
        Container c = null;
        Place p = null;

        if(entity instanceof Floor)
            b = ((Floor) entity).getBuilding();
        else if(entity instanceof Room) {
            f = ((Room) entity).getFloor();
            b = f.getBuilding();
        } else if(entity instanceof Container) {
            r = ((Container) entity).getRoom();
            f = r.getFloor();
            b = f.getBuilding();
        } else if(entity instanceof Place) {
            c = ((Place) entity).getContainer();
            r = c.getRoom();
            f = r.getFloor();
            b = f.getBuilding();
        } else if(entity instanceof Item) {
            Item i = (Item) entity;
            p = i.getPlace();
            if(p!=null) {
                c = p.getContainer();
                r = c.getRoom();
            } else
                r = i.getRoom();
            f = r.getFloor();
            b = f.getBuilding();
        }
        buildingField.setValue(b);
        floorField.setValue(f);
        roomField.setValue(r);
        containerField.setValue(c);
        placeField.setValue(p);
    }

    private void loadData(InventoryEntity entity) {
        if(entity instanceof Building) {
            nameField.setValue(((Building) entity).getName());
            nameField.setRequired(true);
        } else if(entity instanceof Floor) {
            numberField.setValue(((Floor) entity).getNumber());
        } else if(entity instanceof Room) {
            Room room = (Room) entity;
            numberField.setValue(room.getNumber());
            nameField.setValue(Objects.toString(room.getName(),""));
            nameField.setRequired(false);
        } else if(entity instanceof Container) {
            Container container = (Container) entity;
            numberField.setValue(container.getNumber());
            nameField.setValue(Objects.toString(container.getDescription(),""));
            nameField.setRequired(false);
            containerType.setValue(container.getType());
        } else if(entity instanceof Place) {
            Place place = (Place) entity;
            numberField.setValue(place.getNumber());
            nameField.setValue(Objects.toString(place.getName(),""));
            nameField.setRequired(false);
            placeType.setValue(place.getType());
        } else if(entity instanceof Item) {
            Item item = (Item) entity;
            nameField.setValue(item.getName());
            nameField.setRequired(true);
            descriptionField.setValue(Objects.toString(item.getDescription(),""));
            countField.setValue(item.getCount());
            costField.setValue(item.getCost()); //!
            itemStatusField.setValue(item.getStatus());
            waybillField.setValue(Objects.toString(item.getWaybill(),""));
            factoryField.setValue(Objects.toString(item.getFactory(),""));
            inventoryNumberField.setValue(Objects.toString(item.getNumber(),""));

            incomingDateField.setValue(item.getIncoming()==null?
                    incomingDateField.getEmptyValue() : DateCast.toLocalDate(item.getIncoming()));

            writeoffDateField.setValue(item.getWriteoff()==null?
                    writeoffDateField.getEmptyValue() : DateCast.toLocalDate(item.getWriteoff()));

            sheduledWriteoffDateField.setValue(item.getSheduled_writeoff()==null?
                    sheduledWriteoffDateField.getEmptyValue() : DateCast.toLocalDate(item.getSheduled_writeoff()));

            commissioningDateField.setValue(item.getCommissioning()==null?
                    commissioningDateField.getEmptyValue() : DateCast.toLocalDate(item.getCommissioning()));
            // TODO photos
            //uploadedPhoto.setSrc(new StreamResource());
        }
    }

    private void setData(InventoryEntity entity) throws FloatingItemException, RequiredFieldNotFilledException {
        if(entity instanceof Building) {
            Building building = (Building) entity;
            if(nameField.isEmpty())
                throw new RequiredFieldNotFilledException();
            building.setName(nameField.getValue());
        } else if(entity instanceof Floor) {
            Floor floor = (Floor) entity;
            floor.setBuilding(buildingField.getValue());
            if(numberField.isEmpty())
                throw new RequiredFieldNotFilledException();
            floor.setNumber(numberField.getValue());
        } else if(entity instanceof Room) {
            Room room = (Room) entity;
            room.setFloor(floorField.getValue());
            if(numberField.isEmpty())
                throw new RequiredFieldNotFilledException();
            room.setNumber(numberField.getValue());
            room.setName(nameField.getValue());
        } else if(entity instanceof Container) {
            Container container = (Container) entity;
            container.setRoom(roomField.getValue());
            container.setType(containerType.getValue());
            if(numberField.isEmpty())
                throw new RequiredFieldNotFilledException();
            container.setNumber(numberField.getValue());
            container.setDescription(nameField.getValue());
        } else if(entity instanceof Place) {
            Place place = (Place) entity;
            place.setContainer(containerField.getValue());
            place.setType(placeType.getValue());
            if(numberField.isEmpty())
                throw new RequiredFieldNotFilledException();
            place.setNumber(numberField.getValue());
            place.setName(nameField.getValue());
        } else if(entity instanceof Item) {
            Item item = (Item) entity;
            if(placeField.isEmpty() && roomField.isEmpty())
                throw new FloatingItemException();
            if(!placeField.isEmpty()) {
                item.setPlace(placeField.getValue());
                item.setRoom(null);
            }
            else {
                item.setPlace(null);
                item.setRoom(roomField.getValue());
            }

            if(nameField.isEmpty()||countField.isEmpty()||itemStatusField.isEmpty())
                throw new RequiredFieldNotFilledException();
            item.setName(nameField.getValue());
            item.setDescription(descriptionField.getValue());
            item.setCount(countField.getValue());
            item.setCost(costField.getValue());
            item.setStatus(itemStatusField.getValue());
            item.setWaybill(waybillField.getValue());
            item.setFactory(factoryField.getValue());
            item.setNumber(inventoryNumberField.getValue());
            item.setIncoming(DateCast.toDate(incomingDateField.getValue()));
            item.setWriteoff(DateCast.toDate(writeoffDateField.getValue()));
            item.setSheduled_writeoff(DateCast.toDate(sheduledWriteoffDateField.getValue()));
            item.setCommissioning(DateCast.toDate(commissioningDateField.getValue()));
            // TODO photos
        }
    }

    private InventoryEntity create(InventoryEntity parent, Class<? extends InventoryEntity> targetType)
            throws NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        if(parent==null)
            return new Building();
        return targetType.getConstructor(parent.getClass()).newInstance(parent);
    }

    private void add(InventoryEntity entity)
            throws FloatingItemException,
            RequiredFieldNotFilledException {

        setData(entity);
        if(entity instanceof Building)
            buildingService.add((Building) entity);
        else if(entity instanceof Floor)
            floorService.add((Floor) entity);
        else if(entity instanceof Room)
            roomService.add((Room) entity);
        else if(entity instanceof Container)
            containerService.add((Container) entity);
        else if(entity instanceof Place)
            placeService.add((Place) entity);
        else if(entity instanceof Item)
            itemService.add((Item) entity);
    }

    private void edit(InventoryEntity entity)
            throws FloatingItemException,
            EntityNotFoundException,
            RequiredFieldNotFilledException {

        setData(entity);
        if(entity instanceof Building)
            buildingService.edit((Building) entity);
        else if(entity instanceof Floor)
            floorService.edit((Floor) entity);
        else if(entity instanceof Room)
            roomService.edit((Room) entity);
        else if(entity instanceof Container)
            containerService.edit((Container) entity);
        else if(entity instanceof Place)
            placeService.edit((Place) entity);
        else if(entity instanceof Item)
            itemService.edit((Item) entity);
    }

    @Setter
    private Grid<Item> grid;
    @Setter
    private TreeGrid<InventoryEntity> tree;

    private InventoryEntity target, parent;

    public void show(Mode mode, InventoryEntity entity) {
        form.removeAll();
        if (mode == Mode.ADD) {
            confirmButton.setText("Добавить");
            confirmButton.setIcon(VaadinIcon.PLUS.create());

            parent = entity;
            // target - child of entity
            try {
                if (entity == null) {
                    target = create(entity, Building.class);
                    header.setText("Добавить здание");
                } else if (entity instanceof Building) {
                    target = create(entity, Floor.class);
                    header.setText("Добавить этаж");
                } else if (entity instanceof Floor) {
                    target = create(entity, Room.class);
                    header.setText("Добавить кабинет");
                } else if (entity instanceof Room) {
                    target = create(entity, Container.class); //TODO adding Items to Rooms
                    header.setText("Добавить шкаф/стеллаж");
                } else if (entity instanceof Container) {
                    target = create(entity, Place.class);
                    header.setText("Добавить полку/позицию");
                } else {
                    target = create(entity, Item.class);
                    header.setText("Добавить объект");
                }
            } catch (Exception ignored) { }

            confirmButton.addClickListener(event -> {
                try {
                    add(target);
                    if(target instanceof Item)
                        grid.getDataProvider().refreshAll();
                    else if(target instanceof Building)
                        tree.getDataProvider().refreshAll();
                    else
                        tree.getDataProvider().refreshItem(parent, true);
                    showNotification("Успешно добавлено " + InventoryEntityNames.get(target));
                    close();
                } catch (FloatingItemException floatingItemException) {
                    showInfoMessage("Ошибка", VaadinIcon.WARNING.create(),
                            "Укажите местоположение объекта. Объект может находиться в шкафу, " +
                                    "на стеллаже или в комнате.");
                } catch (RequiredFieldNotFilledException requiredFieldNotFilledException) {
                    showNotification("Заполните все обязательные поля.");
                }
            });
        } else {// target = entity
            header.setText("Редактировать");
            confirmButton.setText("Сохранить");
            confirmButton.setIcon(IronIcons.SAVE.create());
            loadData(entity);
            target = entity;
            confirmButton.addClickListener(event -> {
                try {
                    edit(target);
                    if(target instanceof Item)
                        grid.getDataProvider().refreshItem((Item) target);
                    else
                        tree.getDataProvider().refreshItem(target);
                    showNotification("Успешно отредактировано " + InventoryEntityNames.get(target));
                    close();
                } catch (FloatingItemException floatingItemException) {
                    showInfoMessage("Ошибка", VaadinIcon.WARNING.create(),
                            "Укажите местоположение объекта. Объект может находиться в шкафу, " +
                                    "на стеллаже или в комнате.");
                } catch (EntityNotFoundException e) {
                    showInfoMessage("Ошибка", VaadinIcon.WARNING.create(),
                            "Редактируемый объект не найден. Возможно, он был удалён.");
                } catch (RequiredFieldNotFilledException requiredFieldNotFilledException) {
                    showNotification("Заполните все обязательные поля.");
                }
            });

        }

        if(target==null || target instanceof Building) {
            form.setResponsiveSteps(columns.get(2));
            form.add(header, 2);
            form.add(nameField, 2);
            /*
            * [           header          ]
            * [         nameField         ]
            *
            * [confirmButton][cancelButton]
            * */
        }
        else if(target instanceof Floor) {
            form.setResponsiveSteps(columns.get(2));
            form.add(header, 2);
            form.add(buildingField, numberField);
            /*
            * [           header          ]
            * [buildingField][numberField ]
            *
            * [confirmButton][cancelButton]
            * */
        }
        else if(target instanceof Room) {
            form.setResponsiveSteps(columns.get(2));
            form.add(header, 2);
            form.add(buildingField, floorField);
            form.add(numberField);
            form.add(nameField, 1);
            /*
            * [           header          ]
            * [buildingField][ floorField ]
            * [ numberField ][numberField ]
            *
            * [confirmButton][cancelButton]
            * */
        }
        else if(target instanceof Container) {
            form.setResponsiveSteps(columns.get(3));
            form.add(header, 3);
            form.add(buildingField, floorField, roomField);
            form.add(containerType);
            form.add(numberField);
            form.add(nameField, 1);
            form.add(interval);
            /*
            * [                 header                  ]
            * [buildingField][ floorField ][  roomField ]
            * [containerType][numberField ][  nameField ]
            *
            * [------------][confirmButton][cancelButton]
            * */
        }
        else if(target instanceof Place) {
            form.setResponsiveSteps(columns.get(4));
            form.add(header, 4);
            form.add(buildingField, floorField, roomField, containerField);
            form.add(placeType);
            form.add(numberField);
            form.add(nameField, 2);
            form.add(interval, 2);
            /*
            * [                         header                          ]
            * [buildingField][ floorField ][  roomField ][containerField]
            * [  placeType  ][numberField ][         nameField          ]
            *
            * [---------------------------][confirmButton][cancelButton ]
            * */
        }
        else {
            form.add(header);
            form.add(buildingField, floorField, roomField, containerField, placeField);
            form.add(nameField,descriptionField,countField,costField,itemStatusField,
                    inventoryNumberField,waybillField,factoryField,
                    incomingDateField,writeoffDateField,sheduledWriteoffDateField,commissioningDateField,
                    photoUpload,uploadedPhoto);
            form.add(interval); // for buttons


            form.setResponsiveSteps(columns.get(5));
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

            /*
            * |      1      ||      2     ||     3      ||       4       ||        5        |
            *
            * [                                 header                                      ]
            * [buildingField][ floorField ][  roomField ][containerField ][    placeField   ]
            * [         nameField         ][                    descriptionField            ]
            * [ countField  ][        costField         ][         itemStatusField          ]
            * [   inventoryNumberField    ][        waybillField         ][   factoryField  ]
            * [     incomingDateField     ][writeoffD.F.][sheduledW/oD.F.][commissioningD.F.]
            *
            * [               photoUpload               ]                 [  uploadedPhoto  ]
            *
            * [-----------------------------------------][ confirmButton ][  cancelButton   ]
            * */
        }
        form.add(confirmButton, cancelButton);
        preparePathBoxes(target);
        // ...
        open();
    }

    private void showInfoMessage(String title, Icon icon, String message) {
        MessageDialog dialog = new MessageDialog().setTitle(title, icon).setMessage(message);
        dialog.addButton().text("OK").closeOnClick();
        dialog.open();
    }

    private void showNotification(String text) {
        notification.setText(text);
        notification.open();
    }
}
