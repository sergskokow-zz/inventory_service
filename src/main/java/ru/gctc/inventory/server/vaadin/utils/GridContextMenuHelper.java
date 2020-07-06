package ru.gctc.inventory.server.vaadin.utils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import ru.gctc.inventory.server.db.entities.InventoryEntity;

public interface GridContextMenuHelper {
    void onMenuClick(GridContextMenu.GridContextMenuItemClickEvent<? extends InventoryEntity> event);

    static <E extends InventoryEntity> void rightClickSelection(Grid<E> grid, E clickedItem) {
        if(!grid.getSelectedItems().contains(clickedItem)) {
            grid.deselectAll();
            grid.select(clickedItem);
        }
    }
}
