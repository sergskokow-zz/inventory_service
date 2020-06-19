package ru.gctc.inventory.server.db.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.entities.Place;
import ru.gctc.inventory.server.db.repos.PlaceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService extends InventoryEntityService<Place, PlaceRepository> implements ContainsItemsEntityService<Place> {

    public PlaceService(PlaceRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(Place inventoryEntity) {
        Hibernate.initialize(inventoryEntity.getItems());
        return inventoryEntity.getItems();
    }

    @Override
    public int getChildCount(Place inventoryEntity) {
        if(inventoryEntity.getItems()==null)
            return 0;
        return inventoryEntity.getItems().size();
    }

    @Override
    public boolean hasChildren(Place inventoryEntity) {
        return inventoryEntity.getItems()!=null && !inventoryEntity.getItems().isEmpty();
    }

    @Override
    public Optional<InventoryEntity> getParent(Place inventoryEntity) {
        return Optional.of(inventoryEntity.getContainer());
    }

    @Override
    public int itemCount(long entityId) {
        return getChildCount(entityId);
    }

    @Override
    public List<Item> getAllItems(long entityId) {
        return (List<Item>) getChildren(entityId);
    }
}
