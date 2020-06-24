package ru.gctc.inventory.server.vaadin.utils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.data.selection.SelectionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.services.dto.Filters;
import ru.gctc.inventory.server.vaadin.providers.ItemsDataProviderFactory;

import java.util.Optional;

@Component
public class TreeSelectionListener implements SelectionListener
        <Grid<InventoryEntity>, InventoryEntity> {

    private ItemsDataProviderFactory itemsDataProviderFactory;
    @Autowired
    public void setItemsDataProviderFactory(ItemsDataProviderFactory itemsDataProviderFactory) {
        this.itemsDataProviderFactory = itemsDataProviderFactory;
    }

    private Grid<Item> grid;
    public void setGrid(Grid<Item> grid) {
        this.grid = grid;
    }

    private TextField nameFilter, numberFilter, waybillFilter, factoryFilter;
    public void setFilters(TextField nameFilter, TextField numberFilter, TextField waybillFilter, TextField factoryFilter) {
        this.nameFilter = nameFilter;
        this.numberFilter = numberFilter;
        this.waybillFilter = waybillFilter;
        this.factoryFilter = factoryFilter;
    }

    @Override
    public void selectionChange(SelectionEvent<Grid<InventoryEntity>,InventoryEntity> event) {
        Optional<InventoryEntity> selectedGridItem = event.getFirstSelectedItem();
        if(selectedGridItem.isPresent()) {
            InventoryEntity selected = selectedGridItem.get();
            grid.setDataProvider(itemsDataProviderFactory.filter(selected,
                    new Filters(nameFilter.getValue(),
                            numberFilter.getValue(),
                            waybillFilter.getValue(),
                            factoryFilter.getValue())));
        }
    }
}
