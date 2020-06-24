package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.olli.ClipboardHelper;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.vaadin.providers.InventoryEntityDataProvider;
import ru.gctc.inventory.server.vaadin.providers.ItemsDataProviderFactory;
import ru.gctc.inventory.server.vaadin.utils.CommonFilterFieldListener;
import ru.gctc.inventory.server.vaadin.utils.InventoryEntityNames;
import ru.gctc.inventory.server.vaadin.utils.TreeSelectionListener;

import java.text.DecimalFormat;

@Route(value = "",layout = MainView.class)
@Component
@UIScope
public class Inventory extends Div {
    private final SplitLayout splitLayout = new SplitLayout();
    private final TreeGrid<InventoryEntity> tree = new TreeGrid<>();
    private final Grid<Item> grid = new Grid<>();

    @Autowired
    public Inventory(InventoryEntityDataProvider provider, ItemsDataProviderFactory itemsDataProviderFactory,
                     TreeSelectionListener treeSelectionListener, CommonFilterFieldListener commonFilterFieldListener,
                     DeleteConfirmDialog deleteConfirmDialog, Editor editor) {
        editor.setTree(tree);
        editor.setGrid(grid);

        /* main layout */
        setSizeFull();
        add(splitLayout);
        splitLayout.setSizeFull();

        /* TREE */
        tree.addHierarchyColumn(InventoryEntityNames::get);
        tree.setDataProvider(provider);
        treeSelectionListener.setGrid(grid);
        tree.addSelectionListener(treeSelectionListener);
        tree.setSizeFull();
        //tree.setSelectionMode(Grid.SelectionMode.MULTI);
        splitLayout.addToPrimary(tree);


        VerticalLayout gridLayout = new VerticalLayout();

        TextField searchField = new TextField();
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setPlaceholder("Поиск по имени, инвентарному номеру, номеру накладной получения, заводскому номеру");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(event -> {
            var searchQuery = event.getHasValue();
            tree.setVisible(searchQuery.isEmpty());
            if(!searchQuery.isEmpty()) {
                grid.setDataProvider(itemsDataProviderFactory.find(searchQuery.getValue()));
            }
        });

        /* GRID */

        /* for search results */
        var pathColumn = grid.addColumn(Item::getName).setHeader("Местонахождение");
        pathColumn.setVisible(false);

        /* common */
        var nameColumn = grid.addColumn(Item::getName, "name").setHeader("Наименование");
        grid.addColumn(Item::getCount, "count").setHeader("Количество");
        DecimalFormat moneyFormat = new DecimalFormat();
        moneyFormat.setGroupingSize(3);
        moneyFormat.setMinimumFractionDigits(2);
        grid.addColumn(item -> {
            if(item.getCost()!=null)
                return moneyFormat.format(item.getCost());
            else return null;
        },"cost").setHeader("Стоимость, ₽");
        grid.addColumn(item -> switch (item.getStatus()) {
            case IN_USE -> "В эксплуатации";
            case WRITTEN_OFF -> "Списано";
            case TRANSFERRED -> "Передано на отв. хранение";
        }, "status").setHeader("Статус");
        /* numbers */
        var waybillColumn = grid.addColumn(Item::getWaybill,"waybill").setHeader("№ накладной получения");
        var inventoryColumn = grid.addColumn(Item::getNumber,"number").setHeader("Инвентарный номер");
        var factoryColumn = grid.addColumn(Item::getFactory,"factory").setHeader("Заводской номер");
        /* dates */
        grid.addColumn(Item::getInventory,"inventory").setHeader("Дата добавления");
        grid.addColumn(Item::getIncoming,"incoming").setHeader("Дата получения");
        grid.addColumn(Item::getCommissioning,"commissioning").setHeader("Ввод в эксплуатацию");
        grid.addColumn(Item::getWriteoff,"writeoff").setHeader("Дата списания");
        grid.addColumn(Item::getSheduled_writeoff,"sheduled_writeoff").setHeader("Дата планового списания");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setMultiSort(true);
        grid.setColumnReorderingAllowed(true);
        grid.getColumns().forEach(itemColumn -> itemColumn.setResizable(true));

        /* filters */
        HeaderRow filters = grid.appendHeaderRow();

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

        treeSelectionListener.setFilters(nameFilter,inventoryNumFilter,waybillNumFilter,factoryNumFilter);
        commonFilterFieldListener.wire(tree,grid,nameFilter,inventoryNumFilter,waybillNumFilter,factoryNumFilter);

        gridLayout.add(searchField, grid);
        searchField.setWidthFull();
        splitLayout.addToSecondary(gridLayout);
        gridLayout.setSizeFull();
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        splitLayout.setSplitterPosition(25.0);

        /* CONTEXT MENU */
        var contextMenu = grid.addContextMenu();

        var addItemMenu = contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.PLUS),new Label("Добавить...")));
        addItemMenu.addMenuItemClickListener(event -> {
            if(tree.getSelectedItems().iterator().hasNext())
                editor.show(Editor.Mode.ADD, tree.getSelectedItems().iterator().next());
            else
                editor.show(Editor.Mode.ADD, null);
        });

        var editItemMenu = contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.EDIT),new Label("Редактировать...")));
        editItemMenu.addMenuItemClickListener(event -> editor.show(Editor.Mode.EDIT, event.getItem().orElse(null)));

        contextMenu.add(new Hr());
        var reportMenu = contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.FILE_TEXT),new Label("Подготовить отчёт")));
        var reportFormatsMenu = reportMenu.getSubMenu();

        reportFormatsMenu.addItem("Microsoft Word  (*.docx)").addMenuItemClickListener(event -> {
            // TODO docx reports
        });
        reportFormatsMenu.addItem("Microsoft Excel (*.xlsx)").addMenuItemClickListener(event -> {
            // TODO xlsx reports
        });


        var qrCodeMenu = contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.QRCODE),new Label("QR-коды..."))).getSubMenu();

        qrCodeMenu.addItem("Скачать QR-коды для печати").addMenuItemClickListener(event -> {
            // TODO qr
        });
        qrCodeMenu.add(new Hr());

        /* copy link */
        TextField linkField = new TextField("Ссылка на элемент");
        linkField.setAutoselect(true);
        linkField.setReadOnly(true);
        Button linkCopyButton = new Button("Копировать", VaadinIcon.CLIPBOARD_TEXT.create());
        linkCopyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ClipboardHelper clipboardHelper = new ClipboardHelper("", linkCopyButton);
        linkField.addValueChangeListener(event -> clipboardHelper.setContent(event.getValue()));
        HorizontalLayout linkLayout = new HorizontalLayout();
        linkLayout.addAndExpand(linkField, clipboardHelper);
        linkLayout.setAlignItems(FlexComponent.Alignment.END);
        qrCodeMenu.add(linkLayout);


        contextMenu.add(new Hr());
        var deleteMenu = contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.TRASH),new Label("Удалить")));
        deleteMenu.addMenuItemClickListener(event -> deleteConfirmDialog.show(grid.getSelectedItems()));

        contextMenu.addGridContextMenuOpenedListener(event -> {
            event.getItem().ifPresent(item -> { // Right-click selection
                linkField.setValue(InventoryEntityNames.get(item)); // TODO links
                if(!grid.getSelectedItems().contains(item))
                    grid.select(item);
            });
            var selectedItems = grid.getSelectedItems();
            if(selectedItems.isEmpty()) {
                addItemMenu.setVisible(true);
                editItemMenu.setVisible(false);
                reportMenu.setVisible(true);
                linkLayout.setVisible(false);
                deleteMenu.setVisible(false);
            } else if(selectedItems.size()==1) {
                addItemMenu.setVisible(false);
                editItemMenu.setVisible(true);
                reportMenu.setVisible(true);
                linkLayout.setVisible(true);
                deleteMenu.setVisible(true);
            } else {
                addItemMenu.setVisible(false);
                editItemMenu.setVisible(false);
                reportMenu.setVisible(true);
                linkLayout.setVisible(false);
                deleteMenu.setVisible(true);
            }
        });

        /* TREE CONTEXT MENU */
        // TODO fix duplication!
        var treeContextMenu = tree.addContextMenu();
        var addContainerMenu = treeContextMenu.addItem("Добавить...");
        addContainerMenu.addMenuItemClickListener(event -> {
            if(tree.getSelectedItems().iterator().hasNext())
                editor.show(Editor.Mode.ADD, tree.getSelectedItems().iterator().next());
            else
                editor.show(Editor.Mode.ADD, null);
        });
        var editContainerMenu = treeContextMenu.addItem("Редактировать...");
        editContainerMenu.addMenuItemClickListener(event -> editor.show(Editor.Mode.EDIT, event.getItem().orElse(null)));
        treeContextMenu.addItem("Подготовить отчёт").addMenuItemClickListener(event -> {
            // TODO reports
        });
        treeContextMenu.addItem("Скачать QR-коды для печати").addMenuItemClickListener(event -> {
            // TODO qr
        });
        var deleteContainerMenu = treeContextMenu.addItem("Удалить");
        deleteContainerMenu.addMenuItemClickListener(event -> deleteConfirmDialog.show(tree.getSelectedItems()));
        treeContextMenu.addGridContextMenuOpenedListener(event -> {
            event.getItem().ifPresent(item -> { // Right-click selection
                if(!tree.getSelectedItems().contains(item))
                    tree.select(item);
            });
            if(tree.getSelectedItems().isEmpty()) {
                addContainerMenu.setVisible(true);
                editContainerMenu.setVisible(false);
                deleteContainerMenu.setVisible(false);
            } else if(tree.getSelectedItems().size()==1) {
                addContainerMenu.setVisible(true);
                editContainerMenu.setVisible(true);
                deleteContainerMenu.setVisible(true);
            } else {
                addContainerMenu.setVisible(false);
                editContainerMenu.setVisible(false);
                deleteContainerMenu.setVisible(true);
            }
        });
    }

    // TODO replace this
    private final Notification notification = new Notification();
    private void showNotification(String text) {
        notification.setText(text);
        notification.open();
    }
}
