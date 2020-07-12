package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.*;
import ru.gctc.inventory.server.db.services.dto.Filters;

import java.util.List;

@Repository
public interface ItemRepository extends InventoryRepository<Item> {

    // name,number,waybill,factory Contains ?1 AllIgnoreCase
    @Query("SELECT i FROM Item i WHERE "+
            "upper(i.name) LIKE concat('%',upper(?1),'%') OR "+
            "upper(i.number) LIKE concat('%',upper(?1),'%') OR "+
            "upper(i.waybill) LIKE concat('%',upper(?1),'%') OR "+
            "upper(i.factory) LIKE concat('%',upper(?1),'%')")
    Page<Item> filterAll(String substring, Pageable pageable);

    String filtersQuery = " AND (:#{#filters.name}='' OR upper(i.name) LIKE '%'||:#{#filters.name}||'%')"+
                          " AND (:#{#filters.number}='' OR upper(i.number) LIKE '%'||:#{#filters.number}||'%')"+
                          " AND (:#{#filters.waybill}='' OR upper(i.waybill) LIKE '%'||:#{#filters.waybill}||'%')"+
                          " AND (:#{#filters.factory}='' OR upper(i.factory) LIKE '%'||:#{#filters.factory}||'%')";

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN i.place p " +
            "LEFT JOIN p.container c " +
            "JOIN Room r ON (c.room=r) OR (i.room=r) " +
            "JOIN r.floor f " +
            "WHERE f.building=:building" + filtersQuery)
    Page<Item> findByBuilding(@Param("building") Building building,
                              @Param("filters") Filters filters,
                              Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN i.place p " +
            "LEFT JOIN p.container c " +
            "JOIN Room r ON (c.room=r) OR (i.room=r) " +
            "WHERE r.floor=:floor" + filtersQuery)
    Page<Item> findByFloor(@Param("floor") Floor floor,
                           @Param("filters") Filters filters,
                           Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN i.place p " +
            "LEFT JOIN p.container c " +
            "WHERE ((c.room=:room) OR (i.room=:room))" + filtersQuery)
    Page<Item> findByRoom(@Param("room") Room room,
                          @Param("filters") Filters filters,
                          Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "JOIN i.place p " +
            "WHERE p.container=:container" + filtersQuery)
    Page<Item> findByContainer(@Param("container") Container container,
                               @Param("filters") Filters filters,
                               Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.place=:place" + filtersQuery)
    Page<Item> findByPlace(@Param("place") Place place,
                           @Param("filters") Filters filters,
                           Pageable pageable);

    /* eager loading */

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN i.place p " +
            "LEFT JOIN p.container c " +
            "JOIN Room r ON (c.room=r) OR (i.room=r) " +
            "JOIN r.floor f " +
            "WHERE f.building=:building")
    List<Item> findByBuilding(@Param("building") Building building);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN i.place p " +
            "LEFT JOIN p.container c " +
            "JOIN Room r ON (c.room=r) OR (i.room=r) " +
            "WHERE r.floor=:floor")
    List<Item> findByFloor(@Param("floor") Floor floor);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN i.place p " +
            "LEFT JOIN p.container c " +
            "WHERE ((c.room=:room) OR (i.room=:room))")
    List<Item> findByRoom(@Param("room") Room room);

    @Query("SELECT i FROM Item i " +
            "JOIN i.place p " +
            "WHERE p.container=:container")
    List<Item> findByContainer(@Param("container") Container container);

    List<Item> findByPlace(Place place);

    /* writeoff items */
    @Query("SELECT i FROM Item i " +
            "WHERE i.status='IN_USE' " +
            "AND (i.writeoff < CURRENT_DATE OR i.sheduled_writeoff < CURRENT_DATE)" + filtersQuery)
    Page<Item> findWriteoffItems(@Param("filters") Filters filters, Pageable pageable);
}
