package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.vaadin.utils.CommonFilterFieldListenerFactory;
import ru.gctc.inventory.server.vaadin.utils.DownloadRedirect;
import ru.gctc.inventory.server.vaadin.utils.GridContextMenuHelper;

import java.util.Set;
import java.util.stream.Collectors;

@UIScope
@Component
@Route(value = "writeoff", layout = MainView.class)
@PageTitle("Инвентарь, подлежащий списанию")
public class WriteoffItems extends Div {

    Grid<Item> writeoffItems = new Grid<>();

    @Autowired
    public WriteoffItems(CommonFilterFieldListenerFactory listenerFactory,
                         Viewer viewer, Editor editor,
                         DeleteConfirmDialog deleteConfirmDialog) {

        setSizeFull();
        add(writeoffItems);
        writeoffItems.setSizeFull();

        /* columns */
        var nameColumn = writeoffItems.addColumn(Item::getName, "name").setHeader("Наименование");
        var inventoryColumn = writeoffItems.addColumn(Item::getNumber, "number").setHeader("Инвентарный номер");
        var waybillColumn = writeoffItems.addColumn(Item::getWaybill, "waybill").setHeader("Номер накладной получения");
        var factoryColumn = writeoffItems.addColumn(Item::getFactory, "factory").setHeader("Заводской номер");
        writeoffItems.addColumn(Item::getWriteoff, "writeoff").setHeader("Дата списания");
        writeoffItems.addColumn(Item::getSheduled_writeoff, "sheduled_writeoff").setHeader("Дата планового списания");

        writeoffItems.setSelectionMode(Grid.SelectionMode.MULTI);
        writeoffItems.setMultiSort(true);
        writeoffItems.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        writeoffItems.addItemDoubleClickListener(event -> {
            viewer.setTarget(event.getItem());
            viewer.open();
        });

        /* filters */
        HeaderRow filters = writeoffItems.appendHeaderRow();

        TextField nameFilter = new TextField();
        nameFilter.setPlaceholder("Фильтр");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        filters.getCell(nameColumn).setComponent(nameFilter);

        TextField waybillNumFilter = new TextField();
        waybillNumFilter.setPlaceholder("Фильтр");
        waybillNumFilter.setClearButtonVisible(true);
        waybillNumFilter.setValueChangeMode(ValueChangeMode.EAGER);
        filters.getCell(waybillColumn).setComponent(waybillNumFilter);

        TextField inventoryNumFilter = new TextField();
        inventoryNumFilter.setPlaceholder("Фильтр");
        inventoryNumFilter.setClearButtonVisible(true);
        inventoryNumFilter.setValueChangeMode(ValueChangeMode.EAGER);
        filters.getCell(inventoryColumn).setComponent(inventoryNumFilter);

        TextField factoryNumFilter = new TextField();
        factoryNumFilter.setPlaceholder("Фильтр");
        factoryNumFilter.setClearButtonVisible(true);
        factoryNumFilter.setValueChangeMode(ValueChangeMode.EAGER);
        filters.getCell(factoryColumn).setComponent(factoryNumFilter);

        listenerFactory.set(writeoffItems, nameFilter, waybillNumFilter, inventoryNumFilter, factoryNumFilter);
        writeoffItems.getDataProvider().refreshAll();

        /* context menu */
        var contextMenu = writeoffItems.addContextMenu();
        var updateButton = contextMenu.addItem("Обновить");
        /*var selectAllButton = contextMenu.addItem("Выделить всё/Снять выделение");*/
        contextMenu.add(new Hr());
        var showButton = contextMenu.addItem(new HorizontalLayout(VaadinIcon.PICTURE.create(), new Label("Просмотр")));
        showButton.setVisible(false);
        var editButton = contextMenu.addItem(new HorizontalLayout(VaadinIcon.EDIT.create(), new Label("Редактировать")));
        editButton.setVisible(false);
        var reportMenuButton = contextMenu.addItem(new HorizontalLayout(VaadinIcon.FILE.create(), new Label("Подготовить отчёт")));
        var reportMenu = reportMenuButton.getSubMenu();
        var docxReport = reportMenu.addItem(new HorizontalLayout(VaadinIcon.FILE_O.create(), new Label("Mirosoft Word (*.docx)")));
        var xlsxReport = reportMenu.addItem(new HorizontalLayout(VaadinIcon.FILE_TABLE.create(), new Label("Mirosoft Excel (*.docx)")));
        contextMenu.add(new Hr());
        var deleteButton = contextMenu.addItem(new HorizontalLayout(VaadinIcon.TRASH.create(), new Label("Удалить")));
        deleteButton.setVisible(false);

        /* button listeners */
        updateButton.addMenuItemClickListener(event -> writeoffItems.getDataProvider().refreshAll());
        /*selectAllButton.addMenuItemClickListener(event -> ((GridMultiSelectionModel<Item>)writeoffItems.getSelectionModel()).selectAll());*/
        showButton.addMenuItemClickListener(event -> event.getItem().ifPresent(item -> {
            viewer.setTarget(item);
            viewer.open();
        }));
        editButton.addMenuItemClickListener(event -> event.getItem().ifPresent(item -> editor.show(Editor.Mode.EDIT, item)));
        docxReport.addMenuItemClickListener(event -> {
            var selected = writeoffItems.getSelectedItems();
            if(!selected.isEmpty())
                DownloadRedirect.reportByItems(this, "docx", selected);
            else
                DownloadRedirect.reportByItems(this, "docx", writeoffItems.getDataProvider().fetch(new Query<>()).collect(Collectors.toSet()));
        });
        xlsxReport.addMenuItemClickListener(event -> {
            var selected = writeoffItems.getSelectedItems();
            if(!selected.isEmpty())
                DownloadRedirect.reportByItems(this, "xlsx", selected);
            else
                DownloadRedirect.reportByItems(this, "xlsx", writeoffItems.getDataProvider().fetch(new Query<>()).collect(Collectors.toSet()));
        });
        deleteButton.addMenuItemClickListener(event -> {
            var selected = writeoffItems.getSelectedItems();
            if(!selected.isEmpty())
                deleteConfirmDialog.show(selected, null, writeoffItems);
        });

        /* context menu cases */
        contextMenu.addGridContextMenuOpenedListener(event -> {
            event.getItem().ifPresent(item -> GridContextMenuHelper.rightClickSelection(writeoffItems, item));
            var selected = writeoffItems.getSelectedItems();
            Set.of(showButton, editButton, deleteButton).forEach(button -> button.setVisible(!selected.isEmpty()));
        });
    }
}
