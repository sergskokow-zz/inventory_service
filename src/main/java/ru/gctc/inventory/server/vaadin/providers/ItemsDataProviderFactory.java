package ru.gctc.inventory.server.vaadin.providers;


import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.services.ItemService;
import ru.gctc.inventory.server.db.services.dto.Filters;
import ru.gctc.inventory.server.vaadin.utils.SortFactory;

@Component
public class ItemsDataProviderFactory {
    private final ItemService service;

    @Autowired
    public ItemsDataProviderFactory(ItemService service) {
        this.service = service;
    }

    @Deprecated
    public DataProvider<Item, Void> get(InventoryEntity entity) {
        return DataProvider.fromCallbacks(query ->
                service.getChildren(
                        entity,
                        query.getOffset(),
                        query.getLimit(),
                        new Filters(),
                        SortFactory.get(query.getSortOrders().iterator()))
                        .stream(),
                query -> service.getChildrenCount(entity, new Filters()));
    }

    private ConfigurableFilterDataProvider<Item, Void, String> searchProvider;

    public ConfigurableFilterDataProvider<Item, Void, String> find(String filter) {
        if(searchProvider==null) {
            searchProvider = DataProvider.fromFilteringCallbacks(
                    (Query<Item, String> query) ->
                            service.find(query.getFilter().orElse(""),
                                    query.getOffset(),
                                    query.getLimit(),
                                    SortFactory.get(query.getSortOrders().iterator()))
                                    .stream(),
                    query ->
                            service.countFounded(query.getFilter().orElse("")))
                    .withConfigurableFilter();
        }
        searchProvider.setFilter(filter);
        return searchProvider;
    }



    private ConfigurableFilterDataProvider<Item, Void, Filters> filteredDataProvider;
    private InventoryEntity parent;

    public ConfigurableFilterDataProvider<Item, Void, Filters>
        filter(InventoryEntity parent, Filters filters) {

        if(filteredDataProvider==null || this.parent!=parent) {
            filteredDataProvider = DataProvider.fromFilteringCallbacks(
                    (Query<Item, Filters> query) ->
                            service.getChildren(
                                    parent,
                                    query.getOffset(),
                                    query.getLimit(),
                                    query.getFilter().orElse(new Filters()),
                                    SortFactory.get(query.getSortOrders().iterator()))
                                    .stream(),
                    query ->
                            service.getChildrenCount(
                                    parent,
                                    query.getFilter().orElse(new Filters())))
                    .withConfigurableFilter();
            this.parent = parent;
        }
        filteredDataProvider.setFilter(filters);
        return filteredDataProvider;
    }

    private ConfigurableFilterDataProvider<Item, Void, Filters> writeoffItemsDataProvider;

    public ConfigurableFilterDataProvider<Item, Void, Filters>
    getWriteoffItemsDataProvider(Filters filters) {

        if(writeoffItemsDataProvider==null) {
            writeoffItemsDataProvider = DataProvider.fromFilteringCallbacks(
                    (Query<Item, Filters> query) ->
                            service.getWriteoffItems(
                                    query.getOffset(),
                                    query.getLimit(),
                                    query.getFilter().orElse(new Filters()),
                                    SortFactory.get(query.getSortOrders().iterator()))
                                    .stream(),
                    query ->
                            service.getWriteoffItemsCount(query.getFilter().orElse(new Filters())))
                    .withConfigurableFilter();
        }
        writeoffItemsDataProvider.setFilter(filters);
        return writeoffItemsDataProvider;
    }
}
