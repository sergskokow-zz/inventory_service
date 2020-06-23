package ru.gctc.inventory.server.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List<? extends InventoryEntity> getChildren(InventoryEntity inventoryEntity, int offset, int limit) {
        return null;
    }

    @Override
    public long getChildCount(InventoryEntity inventoryEntity) {
        return 0L;
    }

    @Override
    public boolean hasChildren(InventoryEntity inventoryEntity) {
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

    @Override
    public List<Item> filter(FilteringBy field, Place entity, String substring, int offset, int limit) {
        Pageable interval = PageRequest.of(offset, limit);
        Page<Item> pagedItems = switch (field) {
            case NAME -> itemRepository.findAllByPlaceAndNameContainingIgnoreCase(entity, substring, interval);
            case NUMBER -> itemRepository.findAllByPlaceAndNumberContainingIgnoreCase(entity, substring, interval);
            case WAYBILL_NUMBER -> itemRepository.findAllByPlaceAndWaybillContainingIgnoreCase(entity, substring, interval);
            case FACTORY_NUMBER -> itemRepository.findAllByPlaceAndFactoryContainingIgnoreCase(entity, substring, interval);
        };
        return pagedItems.getContent();
    }

    @Override
    public long countFiltered(FilteringBy field, Place entity, String substring) {
        return switch (field) {
            case NAME -> itemRepository.countAllByPlaceAndNameContainingIgnoreCase(entity, substring);
            case NUMBER -> itemRepository.countAllByPlaceAndNumberContainingIgnoreCase(entity, substring);
            case WAYBILL_NUMBER -> itemRepository.countAllByPlaceAndWaybillContainingIgnoreCase(entity, substring);
            case FACTORY_NUMBER -> itemRepository.countAllByPlaceAndFactoryContainingIgnoreCase(entity, substring);
        };
    }
}
