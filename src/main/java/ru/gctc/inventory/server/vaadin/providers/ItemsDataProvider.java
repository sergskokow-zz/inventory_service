package ru.gctc.inventory.server.vaadin.providers;


import com.vaadin.flow.data.provider.DataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.ContainsItems;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.services.ContainsItemsEntityService;

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
}
