package ru.gctc.inventory.server.db.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.entities.Room;
import ru.gctc.inventory.server.db.repos.RoomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService extends InventoryEntityService<Room, RoomRepository> implements ContainsItemsEntityService<Room> {

    public RoomService(RoomRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(Room inventoryEntity) {
        List<InventoryEntity> children = new ArrayList<>();
        Hibernate.initialize(inventoryEntity.getContainers());
        children.addAll(inventoryEntity.getContainers());
        return children;
    }

    @Override
    public int getChildCount(Room inventoryEntity) {
        if(inventoryEntity.getContainers()==null)
            return 0;
        return inventoryEntity.getContainers().size();
    }

    @Override
    public boolean hasChildren(Room inventoryEntity) {
        return inventoryEntity.getContainers()!=null && !inventoryEntity.getContainers().isEmpty();
    }

    @Override
    public Optional<InventoryEntity> getParent(Room inventoryEntity) {
        return Optional.of(inventoryEntity.getFloor());
    }

    @Override
    public int itemCount(long entityId) {
        return getById(entityId).orElseThrow().getItems().size();
    }

    @Override
    public List<Item> getAllItems(long entityId) {
        Room room = getById(entityId).orElseThrow();
        Hibernate.initialize(room.getItems());
        return room.getItems();
    }
}
