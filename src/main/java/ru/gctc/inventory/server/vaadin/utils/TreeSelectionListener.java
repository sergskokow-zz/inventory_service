package ru.gctc.inventory.server.vaadin.utils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.data.selection.SelectionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.ContainsItems;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.vaadin.providers.InventoryEntityManager;
import ru.gctc.inventory.server.vaadin.providers.ItemsDataProvider;

import java.util.List;
import java.util.Optional;

@Component
public class TreeSelectionListener implements SelectionListener
        <Grid<InventoryEntityManager<? extends InventoryEntity>>,
                InventoryEntityManager<? extends InventoryEntity>> {

    private ItemsDataProvider itemsDataProvider;
    @Autowired
    public void setItemsDataProvider(ItemsDataProvider itemsDataProvider) {
        this.itemsDataProvider = itemsDataProvider;
    }

    private Grid<InventoryEntityManager<Item>> grid;
    public void setGrid(Grid<InventoryEntityManager<Item>> grid) {
        this.grid = grid;
    }

    @Override
    public void selectionChange(SelectionEvent<Grid<InventoryEntityManager<? extends InventoryEntity>>,
            InventoryEntityManager<? extends InventoryEntity>> event) {

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
    }
}
