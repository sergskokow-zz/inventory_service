package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.entities.Place;
import ru.gctc.inventory.server.db.entities.Room;

@Repository
public interface ItemRepository extends InventoryRepository<Item> {

    // name,number,waybill,factory Contains ?1 AllIgnoreCase
    String filterQuery = "upper(i.name) like upper(concat('%',concat(?1, '%'))) or "+
            "upper(i.number) like upper(concat('%',concat(?1, '%'))) or "+
            "upper(i.waybill) like upper(concat('%',concat(?1, '%'))) or "+
            "upper(i.factory) like upper(concat('%',concat(?1, '%')))";

    @Query("select i from Item i where "+ filterQuery)
    Page<Item> filter(String substring, Pageable pageable);

    @Query("select count(i) from Item i where "+ filterQuery)
    long counter(String substring); //TODO fix 2x search

    Page<Item> findAllByRoom(Room room, Pageable pageable);

    Page<Item> findAllByPlace(Place place, Pageable pageable);

    long countAllByRoom(Room room);

    long countAllByPlace(Place place);

    boolean existsItemByRoom(Room room);

    boolean existsItemByPlace(Place place);

    // 🤔
    /* room filters */

    Page<Item> findAllByRoomAndNameContainingIgnoreCase(Room room, String substring, Pageable pageable);

    long countAllByRoomAndNameContainingIgnoreCase(Room room, String substring);

    Page<Item> findAllByRoomAndNumberContainingIgnoreCase(Room room, String substring, Pageable pageable);

    long countAllByRoomAndNumberContainingIgnoreCase(Room room, String substring);

    Page<Item> findAllByRoomAndWaybillContainingIgnoreCase(Room room, String substring, Pageable pageable);

    long countAllByRoomAndWaybillContainingIgnoreCase(Room room, String substring);

    Page<Item> findAllByRoomAndFactoryContainingIgnoreCase(Room room, String substring, Pageable pageable);

    long countAllByRoomAndFactoryContainingIgnoreCase(Room room, String substring);

    /* place filters */

    Page<Item> findAllByPlaceAndNameContainingIgnoreCase(Place place, String substring, Pageable pageable);

    long countAllByPlaceAndNameContainingIgnoreCase(Place place, String substring);

    Page<Item> findAllByPlaceAndNumberContainingIgnoreCase(Place place, String substring, Pageable pageable);

    long countAllByPlaceAndNumberContainingIgnoreCase(Place place, String substring);

    Page<Item> findAllByPlaceAndWaybillContainingIgnoreCase(Place place, String substring, Pageable pageable);

    long countAllByPlaceAndWaybillContainingIgnoreCase(Place place, String substring);

    Page<Item> findAllByPlaceAndFactoryContainingIgnoreCase(Place place, String substring, Pageable pageable);

    long countAllByPlaceAndFactoryContainingIgnoreCase(Place place, String substring);
}
