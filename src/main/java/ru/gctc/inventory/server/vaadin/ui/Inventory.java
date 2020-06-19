package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.ContainsItems;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.vaadin.providers.InventoryEntityDataProvider;
import ru.gctc.inventory.server.vaadin.providers.InventoryEntityManager;
import ru.gctc.inventory.server.vaadin.providers.ItemsDataProvider;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Route(value = "",layout = MainView.class)
@Component
@UIScope
public class Inventory extends Div {
    private final SplitLayout splitLayout = new SplitLayout();
    private final TreeGrid<InventoryEntityManager<? extends InventoryEntity>> tree = new TreeGrid<>();
    private final Grid<InventoryEntityManager<Item>> grid = new Grid<>();

    @Autowired
    public Inventory(InventoryEntityDataProvider provider, ItemsDataProvider itemsDataProvider) {

        /* main layout */
        setSizeFull();
        add(splitLayout);
        splitLayout.setSizeFull();

        /* TREE */
        tree.addHierarchyColumn(manager -> manager.getInventoryEntity().toString());
        tree.setDataProvider(provider);
        tree.addSelectionListener(event -> {
            // TODO replace this
            // TODO multiple selection processing
            Optional<InventoryEntityManager<? extends InventoryEntity>> selectedItem =
                    event.getFirstSelectedItem();
            if(selectedItem.isPresent()) {
                InventoryEntityManager<? extends InventoryEntity> selectedManager = selectedItem.get();
                InventoryEntity selectedEntity = selectedManager.getInventoryEntity();
                if(selectedEntity instanceof ContainsItems) {
                    // manual type checking!
                    InventoryEntityManager<ContainsItems> container =
                            (InventoryEntityManager<ContainsItems>) selectedManager;
                    grid.setDataProvider(itemsDataProvider.get(container));
                } else
                    grid.setItems(List.of());
            }
        });
        tree.setSizeFull();
        splitLayout.addToPrimary(tree);

        /* GRID */

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
        var inventoryColumn = grid.addColumn(manager -> manager.getInventoryEntity().getInventory_number()).setHeader("Инвентарный номер");
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

        splitLayout.addToSecondary(grid);
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        splitLayout.setSplitterPosition(25.0);

        /* CONTEXT MENU */
        var contextMenu = grid.addContextMenu();
        contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.PLUS),new Label("Добавить...")));
        contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.EDIT),new Label("Редактировать...")));
        contextMenu.add(new Hr());
        contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.FILE_TEXT),new Label("Подготовить отчёт")));
        var qrCodeMenu = contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.QRCODE),new Label("QR-коды..."))).getSubMenu();

        qrCodeMenu.addItem("Скачать QR-коды для печати");
        qrCodeMenu.add(new Hr());
        /* TODO offline QR generation */
        VerticalLayout qrLayout = new VerticalLayout();
        qrLayout.addAndExpand(new Label("Имя элемента"),
                new Image("http://qrcoder.ru/code/?%DD%EB%E5%EC%E5%ED%F2+%F1%E8%F1%F2%E5%EC%FB+%E8%ED%E2%E5%ED%F2%E0%F0%E8%E7%E0%F6%E8%E8&4&0", "QR-код элемента"));
        qrLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        qrCodeMenu.add(qrLayout);

        contextMenu.add(new Hr());
        contextMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.TRASH),new Label("Удалить")));

    }
}
