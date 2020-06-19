package ru.gctc.inventory.server.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.InventoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
public abstract class InventoryEntityService
        <IE extends InventoryEntity, IR extends InventoryRepository<IE>>
        implements InventoryService<IE> {

    protected final IR repository;

    /* IDEA say "Could not autowire. No beans of 'IR' type found" but it works */
    @Autowired
    public InventoryEntityService(IR repository) {
        this.repository = repository;
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<IE> getAll() {
        List<IE> all = new ArrayList<>();
        repository.findAll().forEach(all::add);
        return all;
    }

    @Override
    public IE add(IE inventoryEntity) {
        return repository.save(inventoryEntity); // TODO if(repo.exists(ie)) throw new ItemAlreadyExistsException();
    }

    @Override
    public IE edit(IE inventoryEntity) {
        return repository.save(inventoryEntity); // TODO if(!repo.existsById(ie.getId())) throw new ItemNotFoundException();
    }

    @Override
    public Optional<IE> getById(long inventoryEntityId) {
        return repository.findById(inventoryEntityId);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(long inventoryEntityId) {
        Optional<IE> target = repository.findById(inventoryEntityId);
        return getChildren(target.orElseThrow());
    }

    @Override
    public int getChildCount(long inventoryEntityId) {
        return getChildCount(repository.findById(inventoryEntityId).orElseThrow());
    }

    @Override
    public boolean hasChildren(long inventoryEntityId) {
        return hasChildren(repository.findById(inventoryEntityId).orElseThrow());
    }

    @Override
    public Optional<InventoryEntity> getParent(long inventoryEntityId) {
        Optional<IE> target = repository.findById(inventoryEntityId);
        return target.map(this::getParent).orElse(null);
    }

    @Override
    public void delete(IE inventoryEntity) {
        repository.delete(inventoryEntity);
    }

    @Override
    public void delete(long inventoryEntityId) {
        repository.deleteById(inventoryEntityId);
    }
}
