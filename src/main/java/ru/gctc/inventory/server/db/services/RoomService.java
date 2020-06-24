package ru.gctc.inventory.server.db.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Floor;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Room;
import ru.gctc.inventory.server.db.repos.RoomRepository;

import java.util.List;

@Service
public class RoomService extends InventoryEntityService<Room, RoomRepository> {

    public RoomService(RoomRepository repository) {
        super(repository);
    }

    @Override
    public List<Room> getChildren(InventoryEntity parent, int offset, int limit) {
        return repository.findAllByFloor(parent, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public long getChildCount(InventoryEntity parent) {
        return repository.countAllByFloor(parent);
    }

    @Override
    public boolean hasChildren(InventoryEntity parent) {
        return repository.existsRoomByFloor(parent);
    }

    @Override
    public Floor getParent(Room entity) {
        return entity.getFloor();
    }
}
