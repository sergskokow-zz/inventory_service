package ru.gctc.inventory.server.db.repos;

import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Photo;

@Repository
public interface PhotoRepository extends InventoryRepository<Photo> { }