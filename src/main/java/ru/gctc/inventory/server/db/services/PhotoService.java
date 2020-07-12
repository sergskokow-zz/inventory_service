package ru.gctc.inventory.server.db.services;

import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Photo;
import ru.gctc.inventory.server.db.repos.PhotoRepository;

import java.util.List;

@Service
public class PhotoService extends InventoryEntityService<Photo, PhotoRepository> {
    public PhotoService(PhotoRepository repository) {
        super(repository);
    }

    @Override
    public List<Photo> getChildren(InventoryEntity parent, int offset, int limit) {
        return null;
    }

    @Override
    public long getChildCount(InventoryEntity parent) {
        return 0;
    }

    @Override
    public boolean hasChildren(InventoryEntity parent) {
        return false;
    }

    @Override
    public InventoryEntity getParent(Photo entity) {
        return null;
    }
}
