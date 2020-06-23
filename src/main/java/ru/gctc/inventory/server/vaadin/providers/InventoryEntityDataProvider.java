package ru.gctc.inventory.server.vaadin.providers;

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
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

    @SneakyThrows
    @Override
    protected Stream<InventoryEntityManager<? extends InventoryEntity>> fetchChildrenFromBackEnd(HierarchicalQuery<InventoryEntityManager<? extends InventoryEntity>, Void> query) {
        InventoryEntityManager<? extends InventoryEntity> manager = query.getParent();
        if(manager==null)
            return buildingService.getAll(query.getOffset(), query.getLimit()).stream().map(factory::build);
        return manager.getInventoryService().getChildren(
                manager.getInventoryEntity(), query.getOffset(), query.getLimit())
                .stream().map(factory::build);
    }

    @SneakyThrows
    @Override
    public int getChildCount(HierarchicalQuery<InventoryEntityManager<? extends InventoryEntity>, Void> query) {
        InventoryEntityManager<? extends InventoryEntity> manager = query.getParent();
        if(manager==null)
            return (int) buildingService.count();
        return (int) manager.getInventoryService().getChildCount(manager.getInventoryEntity());
    }

    @SneakyThrows
    @Override
    public boolean hasChildren(InventoryEntityManager<? extends InventoryEntity> item) {
        return item.getInventoryService().hasChildren(item.getInventoryEntity());
    }
}
