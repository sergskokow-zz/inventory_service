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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.olli.ClipboardHelper;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.vaadin.providers.InventoryEntityDataProvider;
import ru.gctc.inventory.server.vaadin.providers.ItemsDataProviderFactory;
import ru.gctc.inventory.server.vaadin.utils.*;

import java.text.DecimalFormat;
import java.util.Set;

@Route(value = "",layout = MainView.class)
@PageTitle("Система инвентаризации")
@Component
@UIScope
public class Inventory extends Div {
    private final SplitLayout splitLayout = new SplitLayout();
    @Getter
    private final TreeGrid<InventoryEntity> tree = new TreeGrid<>();
    @Getter
    private final Grid<Item> grid = new Grid<>();

    @Autowired
    public Inventory(InventoryEntityDataProvider provider, ItemsDataProviderFactory itemsDataProviderFactory,
                     TreeSelectionListener treeSelectionListener, CommonFilterFieldListenerFactory commonFilterFieldListenerFactory,
                     DeleteConfirmDialog deleteConfirmDialog, Viewer viewer, Editor editor) {
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
        /*var pathColumn = grid.addColumn(Item::getName).setHeader("Местонахождение");
        pathColumn.setVisible(false);*/

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
        grid.addColumn(item -> InventoryEntityNames.itemStatus.get(item.getStatus()), "status").setHeader("Статус");
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
        commonFilterFieldListenerFactory.set(tree,grid,nameFilter,inventoryNumFilter,waybillNumFilter,factoryNumFilter);

        gridLayout.add(searchField, grid);
        searchField.setWidthFull();
        splitLayout.addToSecondary(gridLayout);
        gridLayout.setSizeFull();
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        splitLayout.setSplitterPosition(25.0);

        grid.addItemDoubleClickListener(event -> {
            viewer.setTarget(event.getItem());
            viewer.open();
        });

        /* CONTEXT MENU */
        var gridContextMenu = grid.addContextMenu();
        var treeContextMenu = tree.addContextMenu();

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

        /* item listeners */
        GridContextMenuHelper addListener = event -> {
            var i = tree.getSelectedItems().iterator();
            if(i.hasNext())
                editor.show(Editor.Mode.ADD, i.next());
            else
                editor.show(Editor.Mode.ADD, null);
        };
        GridContextMenuHelper editListener = event ->
                editor.show(Editor.Mode.EDIT, (InventoryEntity) event.getItem().orElse(null));
        GridContextMenuHelper deleteListener = event ->
                deleteConfirmDialog.show((Set<? extends InventoryEntity>) event.getGrid().getSelectedItems(), tree, grid);

        /* make menu */
        var treeAddButton = treeContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.PLUS.create(),new Label("Добавить...")), addListener::onMenuClick);
        var treeEditButton = treeContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.EDIT.create(),new Label("Редактировать...")), editListener::onMenuClick);
        treeContextMenu.add(new Hr());
        var treeReportButton = treeContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.FILE_TEXT.create(),new Label("Подготовить отчёт")));
        var treeReportMenu = treeReportButton.getSubMenu();
        var treeDocxReportButton = treeReportMenu.addItem(
                new HorizontalLayout(VaadinIcon.FILE_TEXT_O.create(),new Label("Microsoft Word (*.docx)")));
        treeDocxReportButton.addMenuItemClickListener(event -> {
            event.getItem().ifPresent(parent -> {
                DownloadRedirect.reportByParent(this, "docx", parent);
            });
        });
        var treeXlsxReportButton = treeReportMenu.addItem(
                new HorizontalLayout(VaadinIcon.FILE_TABLE.create(),new Label("Microsoft Excel (*.xlsx)")));
        treeXlsxReportButton.addMenuItemClickListener(event -> {
            event.getItem().ifPresent(parent -> {
                DownloadRedirect.reportByParent(this, "xlsx", parent);
            });
        });
        var treeQrMenu = treeContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.QRCODE.create(),new Label("QR-коды...")));
        treeQrMenu.addMenuItemClickListener(event -> {
            event.getItem().ifPresent(parent -> DownloadRedirect.qrCodesByParent(this, parent));
        });
        treeContextMenu.add(new Hr());
        var treeDeleteButton = treeContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.TRASH.create(),new Label("Удалить")), deleteListener::onMenuClick);

        var gridOpenButton = gridContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.PICTURE.create(),new Label("Просмотр")));
        gridOpenButton.addMenuItemClickListener(event -> event.getItem().ifPresent(item -> {
            viewer.setTarget(item);
            viewer.open();
        }));
        var gridAddButton = gridContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.PLUS.create(),new Label("Добавить...")), addListener::onMenuClick);
        var gridEditButton = gridContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.EDIT.create(),new Label("Редактировать...")), editListener::onMenuClick);
        gridContextMenu.add(new Hr());
        var gridReportButton = gridContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.FILE_TEXT.create(),new Label("Подготовить отчёт")));
        var gridReportMenu = gridReportButton.getSubMenu();
        var gridDocxReportButton = gridReportMenu.addItem(
                new HorizontalLayout(VaadinIcon.FILE_TEXT_O.create(),new Label("Microsoft Word (*.docx)")));
        gridDocxReportButton.addMenuItemClickListener(event -> {
            var selectedItems = grid.getSelectedItems();
            var selectedParent = tree.getSelectedItems();
            if(!selectedItems.isEmpty())
                DownloadRedirect.reportByItems(this, "docx", selectedItems);
            else if(!selectedParent.isEmpty())
                DownloadRedirect.reportByParent(this, "docx", selectedParent.iterator().next());
        });
        var gridXlsxReportButton = gridReportMenu.addItem(
                new HorizontalLayout(VaadinIcon.FILE_TABLE.create(),new Label("Microsoft Excel (*.xlsx)")));
        gridXlsxReportButton.addMenuItemClickListener(event -> {
            var selectedItems = grid.getSelectedItems();
            var selectedParent = tree.getSelectedItems();
            if(!selectedItems.isEmpty())
                DownloadRedirect.reportByItems(this, "xlsx", selectedItems);
            else if(!selectedParent.isEmpty())
                DownloadRedirect.reportByParent(this, "xlsx", selectedParent.iterator().next());
        });
        var gridQrMenu = gridContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.QRCODE.create(),new Label("QR-коды..."))).getSubMenu();
        var gridQrDownload = gridQrMenu.addItem(
                new HorizontalLayout(VaadinIcon.DOWNLOAD.create(),new Label("Скачать QR-коды")));
        gridQrDownload.addMenuItemClickListener(event -> {
            var selectedItems = grid.getSelectedItems();
            var selectedParent = tree.getSelectedItems();
            if(!selectedItems.isEmpty())
                DownloadRedirect.qrCodesByItems(this, selectedItems);
            else if(!selectedParent.isEmpty())
                DownloadRedirect.qrCodesByParent(this, selectedParent.iterator().next());
        });
        gridQrMenu.add(new Hr());
        gridQrMenu.add(linkLayout);
        gridContextMenu.add(new Hr());
        var gridDeleteButton = gridContextMenu.addItem(
                new HorizontalLayout(VaadinIcon.TRASH.create(),new Label("Удалить")), deleteListener::onMenuClick);


        /* context menu cases */
        treeContextMenu.addGridContextMenuOpenedListener(event -> {
            event.getItem().ifPresent(item -> GridContextMenuHelper.rightClickSelection(tree, item));
            var selected = tree.getSelectedItems();
            if(selected.isEmpty()) {
                treeAddButton.setVisible(true);
                treeEditButton.setVisible(false);
                treeReportButton.setVisible(false);
                treeQrMenu.setVisible(false);
                treeDeleteButton.setVisible(false);
            }
            else if(selected.size()==1) {
                treeAddButton.setVisible(true);
                treeEditButton.setVisible(true);
                treeReportButton.setVisible(true);
                treeQrMenu.setVisible(true);
                treeDeleteButton.setVisible(true);
            }
            else {
                treeAddButton.setVisible(false);
                treeEditButton.setVisible(false);
                treeReportButton.setVisible(true);
                treeQrMenu.setVisible(true);
                treeDeleteButton.setVisible(true);
            }
        });
        gridContextMenu.addGridContextMenuOpenedListener(event -> {
            event.getItem().ifPresent(item -> GridContextMenuHelper.rightClickSelection(grid, item));
            var selected = grid.getSelectedItems();
            if (selected.isEmpty()) {
                gridOpenButton.setVisible(false);
                gridAddButton.setVisible(true);
                gridEditButton.setVisible(false);
                gridReportButton.setVisible(true);
                gridQrDownload.setVisible(true);
                linkLayout.setVisible(false);
                gridDeleteButton.setVisible(false);
            } else if (selected.size() == 1) {
                gridOpenButton.setVisible(true);
                gridAddButton.setVisible(false);
                gridEditButton.setVisible(true);
                gridReportButton.setVisible(true);
                gridQrDownload.setVisible(true);
                linkLayout.setVisible(true);
                linkField.setValue(LinkFactory.get(selected.iterator().next().getId()));
                gridDeleteButton.setVisible(true);
            } else {
                gridOpenButton.setVisible(false);
                gridAddButton.setVisible(false);
                gridEditButton.setVisible(false);
                gridReportButton.setVisible(true);
                gridQrDownload.setVisible(true);
                linkLayout.setVisible(false);
                gridDeleteButton.setVisible(true);
            }
        });
    }
}
