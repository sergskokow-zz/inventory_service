package ru.gctc.inventory.server.vaadin.providers;


import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.ContainsItems;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.services.ContainsItemsEntityService;
import ru.gctc.inventory.server.db.services.ItemService;

@Component
public class ItemsDataProvider {
    private final InventoryEntityManagerFactory factory;

    @Autowired
    public ItemsDataProvider(InventoryEntityManagerFactory factory) {
        this.factory = factory;
    }

    public <CI extends ContainsItems>
                    DataProvider<InventoryEntityManager<Item>, Void>
                    get(InventoryEntityManager<CI> entityManager) {

        ContainsItemsEntityService<CI> containsItemsEntityService =
                (ContainsItemsEntityService<CI>)
                entityManager.getInventoryService();

        return DataProvider.fromCallbacks(query ->
                        containsItemsEntityService
                                .getAllItems(
                                        entityManager.getInventoryEntity(),
                                        query.getOffset(),
                                        query.getLimit())
                                .stream()
                                .map(factory::build)

                , query -> (int) containsItemsEntityService
                        .itemCount(entityManager.getInventoryEntity()));
    }

    private ItemService itemService;
    @Autowired
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    private ConfigurableFilterDataProvider<InventoryEntityManager<Item>, Void, String> searchProvider;

    public ConfigurableFilterDataProvider<InventoryEntityManager<Item>, Void, String> find(String filter) {
        if(searchProvider==null) {
            searchProvider = DataProvider.fromFilteringCallbacks(
                    (Query<InventoryEntityManager<Item>, String> query) ->
                            itemService.findByName(query.getFilter().orElse(""),
                                    query.getOffset(),
                                    query.getLimit())
                                    .stream()
                                    .map(factory::build),
                    (Query<InventoryEntityManager<Item>, String> query) ->
                            (int) itemService.countByName(query.getFilter().orElse("")))
                    .withConfigurableFilter();
        }
        searchProvider.setFilter(filter);
        return searchProvider;
    }
}
