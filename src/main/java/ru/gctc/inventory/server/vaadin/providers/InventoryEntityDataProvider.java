package ru.gctc.inventory.server.vaadin.providers;

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.entities.Place;
import ru.gctc.inventory.server.db.entities.Room;
import ru.gctc.inventory.server.db.services.BuildingService;

import java.util.stream.Stream;

@Component
public class InventoryEntityDataProvider extends AbstractBackEndHierarchicalDataProvider<InventoryEntityManager<? extends InventoryEntity>, Void> {
    private final InventoryEntityManagerFactory factory;
    private final BuildingService buildingService;

    @Autowired
    public InventoryEntityDataProvider(InventoryEntityManagerFactory factory,
                                       BuildingService buildingService) {
        this.factory = factory;
        this.buildingService = buildingService;
    }

    @Override
    protected Stream<InventoryEntityManager<? extends InventoryEntity>> fetchChildrenFromBackEnd(HierarchicalQuery<InventoryEntityManager<? extends InventoryEntity>, Void> query) {
        InventoryEntityManager<? extends InventoryEntity> manager = query.getParent();
        if(manager==null)
            return buildingService.getAll().stream().map(factory::build);
        if(manager.getInventoryEntity() instanceof Room)
            return manager.getInventoryService().getChildren(
                    manager.getInventoryEntity().getId()) /* TODO endpoint interface */
                    .stream().filter(entity -> !(entity instanceof Item)).map(factory::build);
                    // TODO duplicated code
        return manager.getInventoryService().getChildren(
                manager.getInventoryEntity().getId())
                .stream().map(factory::build);
    }

    @Override
    public int getChildCount(HierarchicalQuery<InventoryEntityManager<? extends InventoryEntity>, Void> query) {
        InventoryEntityManager<? extends InventoryEntity> manager = query.getParent();
        if(manager==null)
            return (int) buildingService.count();
        return manager.getInventoryService().getChildCount(manager.getInventoryEntity().getId());
    }

    @Override
    public boolean hasChildren(InventoryEntityManager<? extends InventoryEntity> item) {
        if(item.getInventoryEntity() instanceof Place) // TODO endpoint interface
            return false;
        return item.getInventoryService().hasChildren(item.getInventoryEntity().getId());
    }
}
