package ru.gctc.inventory.server.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List<? extends InventoryEntity> getChildren(InventoryEntity inventoryEntity, int offset, int limit) {
        return containerRepository.findAllByRoom(inventoryEntity, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public long getChildCount(InventoryEntity inventoryEntity) {
        return containerRepository.countAllByRoom(inventoryEntity);
    }

    @Override
    public boolean hasChildren(InventoryEntity inventoryEntity) {
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

    @Override
    public List<Item> filter(FilteringBy field, Room entity, String substring, int offset, int limit) {
        Pageable interval = PageRequest.of(offset, limit);
        Page<Item> pagedItems = switch (field) {
            case NAME -> itemRepository.findAllByRoomAndNameContainingIgnoreCase(entity, substring, interval);
            case NUMBER -> itemRepository.findAllByRoomAndNumberContainingIgnoreCase(entity, substring, interval);
            case WAYBILL_NUMBER -> itemRepository.findAllByRoomAndWaybillContainingIgnoreCase(entity, substring, interval);
            case FACTORY_NUMBER -> itemRepository.findAllByRoomAndFactoryContainingIgnoreCase(entity, substring, interval);
        };
        return pagedItems.getContent();
    }

    @Override
    public long countFiltered(FilteringBy field, Room entity, String substring) {
        return switch (field) {
            case NAME -> itemRepository.countAllByRoomAndNameContainingIgnoreCase(entity, substring);
            case NUMBER -> itemRepository.countAllByRoomAndNumberContainingIgnoreCase(entity, substring);
            case WAYBILL_NUMBER -> itemRepository.countAllByRoomAndWaybillContainingIgnoreCase(entity, substring);
            case FACTORY_NUMBER -> itemRepository.countAllByRoomAndFactoryContainingIgnoreCase(entity, substring);
        };
    }
}
