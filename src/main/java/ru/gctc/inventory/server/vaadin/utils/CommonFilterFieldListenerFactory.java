package ru.gctc.inventory.server.vaadin.utils;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.services.dto.Filters;
import ru.gctc.inventory.server.vaadin.providers.ItemsDataProviderFactory;

import java.util.List;

@Component
public class CommonFilterFieldListenerFactory {
    private final ItemsDataProviderFactory factory;
    @Autowired
    public CommonFilterFieldListenerFactory(ItemsDataProviderFactory factory) {
        this.factory = factory;
    }

    public void set(TreeGrid<InventoryEntity> tree,
                    Grid<Item> grid,
                    TextField nameField,
                    TextField numberField,
                    TextField waybillField,
                    TextField factoryField) {

        HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextField,String>>
                listener = event -> {
            if(!tree.getSelectedItems().isEmpty())
                grid.setDataProvider(factory.filter(tree.getSelectedItems().iterator().next(),
                        new Filters(nameField.getValue(),
                                numberField.getValue(),
                                waybillField.getValue(),
                                factoryField.getValue())));
        };

        List.of(nameField,numberField,waybillField,factoryField).
                forEach(textField -> textField.addValueChangeListener(listener));

        grid.recalculateColumnWidths();
    }

    public void set(Grid<Item> grid,
                    TextField nameField,
                    TextField numberField,
                    TextField waybillField,
                    TextField factoryField) {

        grid.setDataProvider(factory.getWriteoffItemsDataProvider(new Filters())); // init

        HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextField,String>>
                listener = event -> grid.setDataProvider(factory.getWriteoffItemsDataProvider(
                        new Filters(nameField.getValue(),
                                numberField.getValue(),
                                waybillField.getValue(),
                                factoryField.getValue())));

        List.of(nameField,numberField,waybillField,factoryField).
                forEach(textField -> textField.addValueChangeListener(listener));
    }
}
