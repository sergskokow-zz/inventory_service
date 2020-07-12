package ru.gctc.inventory.server.db.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.*;
import ru.gctc.inventory.server.db.repos.ItemRepository;
import ru.gctc.inventory.server.db.services.dto.Filters;

import java.util.List;

@Service
public class ItemService extends InventoryEntityService<Item,ItemRepository> {

    public ItemService(ItemRepository repository) {
        super(repository);
    }

    @Override
    @Deprecated
    public List<Item> getChildren(InventoryEntity parent, int offset, int limit) {
        return null;
    }

    @Override
    @Deprecated
    public long getChildCount(InventoryEntity parent) {
        return 0;
    }

    @Override
    @Deprecated
    public boolean hasChildren(InventoryEntity parent) {
        return false;
    }

    public ContainsItems getParent(Item entity) {
        Place place = entity.getPlace();
        if(place!=null)
            return place;
        return entity.getRoom();
    }

    public List<Item> find(String name, int offset, int limit, Sort sort) {
        return repository.filterAll(name, PageRequest.of(offset, limit, sort)).getContent();
    }

    public int countFounded(String name) {
        return (int) repository.filterAll(name, null).getTotalElements();
    }

    public List<Item> getChildren(InventoryEntity parent, int offset, int limit, Filters filters, Sort sort) {
        if(parent instanceof Building)
            return repository.findByBuilding((Building) parent, filters, PageRequest.of(offset, limit, sort)).getContent();
        if(parent instanceof Floor)
            return repository.findByFloor((Floor) parent, filters, PageRequest.of(offset, limit, sort)).getContent();
        if(parent instanceof Room)
            return repository.findByRoom((Room) parent, filters, PageRequest.of(offset, limit, sort)).getContent();
        if(parent instanceof Container)
            return repository.findByContainer((Container) parent, filters, PageRequest.of(offset, limit, sort)).getContent();
        if(parent instanceof Place)
            return repository.findByPlace((Place)parent, filters, PageRequest.of(offset, limit, sort)).getContent();
        return null;
    }

    public int getChildrenCount(InventoryEntity parent, Filters filters) {
        if(parent instanceof Building)
            return (int) repository.findByBuilding((Building) parent, filters, null).getTotalElements();
        if(parent instanceof Floor)
            return (int) repository.findByFloor((Floor) parent, filters, null).getTotalElements();
        if(parent instanceof Room)
            return (int) repository.findByRoom((Room) parent, filters, null).getTotalElements();
        if(parent instanceof Container)
            return (int) repository.findByContainer((Container) parent, filters, null).getTotalElements();
        if(parent instanceof Place)
            return (int) repository.findByPlace((Place)parent, filters, null).getTotalElements();
        return 0;
    }

    public List<Item> getAllChildren(InventoryEntity parent) {
        if(parent instanceof Building)
            return repository.findByBuilding((Building) parent);
        if(parent instanceof Floor)
            return repository.findByFloor((Floor) parent);
        if(parent instanceof Room)
            return repository.findByRoom((Room) parent);
        if(parent instanceof Container)
            return repository.findByContainer((Container) parent);
        if(parent instanceof Place)
            return repository.findByPlace((Place)parent);
        return null;
    }

    public Iterable<Item> getAllByIds(Iterable<Long> itemIds) {
        return repository.findAllById(itemIds);
    }

    public List<Item> getWriteoffItems(int offset, int limit, Filters filters, Sort sort) {
        return repository.findWriteoffItems(filters, PageRequest.of(offset, limit, sort)).getContent();
    }
    public int getWriteoffItemsCount(Filters filters) {
        return (int) repository.findWriteoffItems(filters, null).getTotalElements();
    }
}
