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
import ru.gctc.inventory.server.vaadin.providers.InventoryEntityManager;
import ru.gctc.inventory.server.vaadin.providers.ItemsDataProvider;
import ru.gctc.inventory.server.vaadin.utils.TreeSelectionListener;

import java.text.DecimalFormat;

@Route(value = "",layout = MainView.class)
@Component
@UIScope
public class Inventory extends Div {
    private final SplitLayout splitLayout = new SplitLayout();
    private final TreeGrid<InventoryEntityManager<? extends InventoryEntity>> tree = new TreeGrid<>();
    private final Grid<InventoryEntityManager<Item>> grid = new Grid<>();

    @Autowired
    public Inventory(InventoryEntityDataProvider provider, ItemsDataProvider itemsDataProvider,
                     TreeSelectionListener treeSelectionListener,
                     Editor editor) {

        /* main layout */
        setSizeFull();
        add(splitLayout);
        splitLayout.setSizeFull();

        /* TREE */
        tree.addHierarchyColumn(manager -> manager.getInventoryEntity().toString());
        tree.setDataProvider(provider);
        treeSelectionListener.setGrid(grid);
        tree.addSelectionListener(treeSelectionListener);
        tree.setSizeFull();
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
            if(!searchQuery.isEmpty())
                grid.setDataProvider(itemsDataProvider.find(searchQuery.getValue()));
        });

        /* GRID */

        /* for search results */
        var pathColumn = grid.addColumn(manager -> manager.getInventoryEntity().getName()).setHeader("Местонахождение").setResizable(true);
        pathColumn.setVisible(false);

        /* common */
        var nameColumn = grid.addColumn(manager -> manager.getInventoryEntity().getName()).setHeader("Наименование").setResizable(true);
        grid.addColumn(manager -> manager.getInventoryEntity().getCount()).setHeader("Количество");
        DecimalFormat moneyFormat = new DecimalFormat();
        moneyFormat.setGroupingSize(3);
        moneyFormat.setMinimumFractionDigits(2);
        grid.addColumn(manager -> moneyFormat.format(manager.getInventoryEntity().getCost())).setHeader("Стоимость, ₽");
        grid.addColumn(manager -> switch (manager.getInventoryEntity().getStatus()) {
            case IN_USE -> "В эксплуатации";
            case WRITTEN_OFF -> "Списано";
            case TRANSFERRED -> "Передано на отв. хранение";
        }).setHeader("Статус");
        /* numbers */
        var waybillColumn = grid.addColumn(manager -> manager.getInventoryEntity().getWaybill()).setHeader("№ накладной получения");
        var inventoryColumn = grid.addColumn(manager -> manager.getInventoryEntity().getNumber()).setHeader("Инвентарный номер");
        var factoryColumn = grid.addColumn(manager -> manager.getInventoryEntity().getFactory()).setHeader("Заводской номер");
        /* dates */
        grid.addColumn(manager -> manager.getInventoryEntity().getInventory()).setHeader("Дата добавления");
        grid.addColumn(manager -> manager.getInventoryEntity().getIncoming()).setHeader("Дата получения");
        grid.addColumn(manager -> manager.getInventoryEntity().getCommissioning()).setHeader("Ввод в эксплуатацию");
        grid.addColumn(manager -> manager.getInventoryEntity().getWriteoff()).setHeader("Дата списания");
        grid.addColumn(manager -> manager.getInventoryEntity().getSheduled_writeoff()).setHeader("Дата планового списания");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setMultiSort(true);
        grid.setColumnReorderingAllowed(true);

        /* filters */
        HeaderRow filters = grid.appendHeaderRow();

        TextField nameFilter = new TextField();
        nameFilter.setPlaceholder("Фильтр");
        filters.getCell(nameColumn).setComponent(nameFilter);
        // TODO listeners

        TextField waybillNumFilter = new TextField();
        waybillNumFilter.setPlaceholder("Фильтр");
        filters.getCell(waybillColumn).setComponent(waybillNumFilter);

        TextField inventoryNumFilter = new TextField();
        inventoryNumFilter.setPlaceholder("Фильтр");
        filters.getCell(inventoryColumn).setComponent(inventoryNumFilter);

        TextField factoryNumFilter = new TextField();
        factoryNumFilter.setPlaceholder("Фильтр");
        filters.getCell(factoryColumn).setComponent(factoryNumFilter);

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
        });

        var editItemMenu = contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.EDIT),new Label("Редактировать...")));
        editItemMenu.addMenuItemClickListener(event -> editor.show(Editor.Mode.EDIT, event.getItem().orElse(null)));

        contextMenu.add(new Hr());
        var reportMenu = contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.FILE_TEXT),new Label("Подготовить отчёт")));
        var qrCodeMenu = contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.QRCODE),new Label("QR-коды..."))).getSubMenu();


        qrCodeMenu.addItem("Скачать QR-коды для печати");
        qrCodeMenu.add(new Hr());

        /* copy link */
        VerticalLayout qrLayout = new VerticalLayout();
        Label itemName = new Label("Имя элемента");
        TextField linkField = new TextField("Ссылка на элемент");
        linkField.setAutoselect(true);
        linkField.setValue("Ссылка на элемент"); // TODO links
        Button linkCopyButton = new Button("Копировать", VaadinIcon.CLIPBOARD_TEXT.create());
        linkCopyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ClipboardHelper clipboardHelper = new ClipboardHelper("", linkCopyButton);
        linkField.addValueChangeListener(event -> clipboardHelper.setContent(event.getValue()));
        HorizontalLayout linkLayout = new HorizontalLayout();
        linkLayout.addAndExpand(linkField, clipboardHelper);

        linkLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        qrLayout.addAndExpand(itemName, linkLayout);
        qrLayout.add(/*new Barcode("Адрес элемента", Barcode.Type.qrcode)*/);
        qrLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        qrCodeMenu.add(qrLayout);


        contextMenu.add(new Hr());
        var deleteMenu = contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.TRASH),new Label("Удалить")));

        contextMenu.addGridContextMenuOpenedListener(event -> {
            event.getItem().ifPresent(item -> { // Right-click selection
                if(!grid.getSelectedItems().contains(item))
                    grid.select(item);
            });
            var selectedItems = grid.getSelectedItems();
            if(selectedItems.isEmpty()) {
                addItemMenu.setVisible(true);
                editItemMenu.setVisible(false);
                reportMenu.setVisible(true);
                qrLayout.setVisible(false);
                deleteMenu.setVisible(false);
            } else if(selectedItems.size()==1) {
                addItemMenu.setVisible(false);
                editItemMenu.setVisible(true);
                reportMenu.setVisible(true);
                qrLayout.setVisible(true);
                deleteMenu.setVisible(true);
            } else {
                addItemMenu.setVisible(false);
                editItemMenu.setVisible(false);
                reportMenu.setVisible(true);
                qrLayout.setVisible(false);
                deleteMenu.setVisible(true);
            }
        });
    }
}
