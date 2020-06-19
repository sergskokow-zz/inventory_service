package ru.gctc.inventory.server.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.entities.Room;
import ru.gctc.inventory.server.db.repos.ContainerRepository;
import ru.gctc.inventory.server.db.repos.ItemRepository;
import ru.gctc.inventory.server.db.repos.RoomRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService extends InventoryEntityService<Room, RoomRepository> implements ContainsItemsEntityService<Room> {
    private ContainerRepository containerRepository;
    private ItemRepository itemRepository;
    @Autowired
    public void setItemRepository(ItemRepository itemRepository, ContainerRepository containerRepository) {
        this.itemRepository = itemRepository;
        this.containerRepository = containerRepository;
    }

    public RoomService(RoomRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(Room inventoryEntity, int offset, int limit) {
        return containerRepository.findAllByRoom(inventoryEntity, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public long getChildCount(Room inventoryEntity) {
        return containerRepository.countAllByRoom(inventoryEntity);
    }

    @Override
    public boolean hasChildren(Room inventoryEntity) {
        return containerRepository.existsContainerByRoom(inventoryEntity);
    }

    @Override
    public Optional<InventoryEntity> getParent(Room inventoryEntity) {
        return Optional.of(inventoryEntity.getFloor());
    }

    @Override
    public long itemCount(Room entity) {
        return itemRepository.countAllByRoom(entity);
    }

    @Override
    public List<Item> getAllItems(Room entity, int offset, int limit) {
        return itemRepository.findAllByRoom(entity, PageRequest.of(offset, limit)).getContent();
    }
}
