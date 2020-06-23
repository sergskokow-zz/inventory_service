package ru.gctc.inventory.server.db.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.repos.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService extends InventoryEntityService<Item, ItemRepository> {

    private final Sort.TypedSort<Item> itemSort = Sort.sort(Item.class);
    public ItemService(ItemRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(InventoryEntity inventoryEntity, int offset, int limit) {
        return null;
    }

    @Override
    public long getChildCount(InventoryEntity inventoryEntity) {
        return 0;
    }

    @Override
    public boolean hasChildren(InventoryEntity inventoryEntity) {
        return false;
    }

    @Override
    public Optional<InventoryEntity> getParent(Item inventoryEntity) {
        InventoryEntity place = inventoryEntity.getPlace();
        if(place!=null)
            return Optional.of(place);
        InventoryEntity room = inventoryEntity.getRoom();
        return Optional.ofNullable(room);
    }

    public List<Item> findByName(String name, int offset, int limit) {
        return repository.filter(name, PageRequest.of(offset, limit)).getContent();
    }

    public long countByName(String name) {
        return repository.counter(name);
    }
}
