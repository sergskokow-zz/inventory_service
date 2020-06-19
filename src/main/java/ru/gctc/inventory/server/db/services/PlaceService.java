package ru.gctc.inventory.server.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.entities.Place;
import ru.gctc.inventory.server.db.repos.ItemRepository;
import ru.gctc.inventory.server.db.repos.PlaceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService extends InventoryEntityService<Place, PlaceRepository> implements ContainsItemsEntityService<Place> {
    private ItemRepository itemRepository;
    @Autowired
    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public PlaceService(PlaceRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(Place inventoryEntity, int offset, int limit) {
        return null;
    }

    @Override
    public long getChildCount(Place inventoryEntity) {
        return 0L;
    }

    @Override
    public boolean hasChildren(Place inventoryEntity) {
        return false;
    }

    @Override
    public Optional<InventoryEntity> getParent(Place inventoryEntity) {
        return Optional.of(inventoryEntity.getContainer());
    }

    @Override
    public long itemCount(Place entity) {
        return itemRepository.countAllByPlace(entity);
    }

    @Override
    public List<Item> getAllItems(Place entity, int offset, int limit) {
        return itemRepository.findAllByPlace(entity, PageRequest.of(offset, limit)).getContent();
    }
}
