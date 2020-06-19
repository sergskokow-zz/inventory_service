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
        return DataProvider.fromCallbacks(query ->
                        ((ContainsItemsEntityService<CI>)
                                entityManager.getInventoryService())
                                .getAllItems(
                                        entityManager.getInventoryEntity().getId(),
                                        query.getOffset(),
                                        query.getLimit())
                                .stream()
                                .map(factory::build)
                , query -> ((ContainsItemsEntityService<CI>)
                        entityManager.getInventoryService())
                        .itemCount(entityManager.getInventoryEntity().getId()));
    }
}
