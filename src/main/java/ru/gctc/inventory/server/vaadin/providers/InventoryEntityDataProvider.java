package ru.gctc.inventory.server.vaadin.providers;

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.*;
import ru.gctc.inventory.server.db.services.*;

import java.util.stream.Stream;

@Component
public class InventoryEntityDataProvider extends AbstractBackEndHierarchicalDataProvider<InventoryEntity, Void> {
    private final BuildingService buildingService;
    private final FloorService floorService;
    private final RoomService roomService;
    private final ContainerService containerService;
    private final PlaceService placeService;

    @Autowired
    public InventoryEntityDataProvider(BuildingService buildingService,
                                       FloorService floorService,
                                       RoomService roomService,
                                       ContainerService containerService,
                                       PlaceService placeService) {
        this.buildingService = buildingService;
        this.floorService = floorService;
        this.roomService =roomService;
        this.containerService = containerService;
        this.placeService = placeService;
    }

    @Override
    protected Stream<InventoryEntity> fetchChildrenFromBackEnd(HierarchicalQuery<InventoryEntity, Void> query) {
        InventoryEntity entity = query.getParent();
        if(entity==null)
            return buildingService.getAll(query.getOffset(), query.getLimit()).stream().map(building -> building);
        if(entity instanceof Building)
            return floorService.getChildren(entity, query.getOffset(), query.getLimit()).stream().map(floor -> floor);
        if(entity instanceof Floor)
            return roomService.getChildren(entity, query.getOffset(), query.getLimit()).stream().map(room -> room);
        if(entity instanceof Room)
            return containerService.getChildren(entity, query.getOffset(), query.getLimit()).stream().map(container -> container);
        if(entity instanceof Container)
            return placeService.getChildren(entity, query.getOffset(), query.getLimit()).stream().map(place -> place);
        return Stream.empty();
    }

    @Override
    public int getChildCount(HierarchicalQuery<InventoryEntity, Void> query) {
        InventoryEntity entity = query.getParent();
        if(entity==null)
            return (int) buildingService.count();
        if(entity instanceof Building)
            return (int) floorService.getChildCount(entity);
        if(entity instanceof Floor)
            return (int) roomService.getChildCount(entity);
        if(entity instanceof Room)
            return (int) containerService.getChildCount(entity);
        if(entity instanceof Container)
            return (int) placeService.getChildCount(entity);
        return 0;
    }

    @Override
    public boolean hasChildren(InventoryEntity item) {
        if(item instanceof Building)
            return floorService.hasChildren(item);
        if(item instanceof Floor)
            return roomService.hasChildren(item);
        if(item instanceof Room)
            return containerService.hasChildren(item);
        if(item instanceof Container)
            return placeService.hasChildren(item);
        return false;
    }
}
