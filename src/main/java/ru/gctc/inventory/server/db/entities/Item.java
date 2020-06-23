package ru.gctc.inventory.server.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
public class Item extends InventoryEntity {
    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    @NotNull
    private String name;

    private String description;

    @Column(nullable = false)
    @ColumnDefault("1")
    @Min(1L)
    @NotNull
    private Integer count;

    @Column(precision = 30, scale = 2)
    @Min(0L)
    private BigDecimal cost;

    @Override
    public String toString() {
        return name;
    }

    public enum Status { IN_USE, WRITTEN_OFF, TRANSFERRED }
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("IN_USE")
    @NotNull
    private Status status;

    private String waybill;

    private String factory;

    private String number;

    @Temporal(TemporalType.TIMESTAMP)
    private Date inventory;

    @Temporal(TemporalType.DATE)
    private Date incoming;

    @Temporal(TemporalType.DATE)
    private Date writeoff;

    @Temporal(TemporalType.DATE)
    private Date sheduled_writeoff;

    @Temporal(TemporalType.DATE)
    private Date commissioning;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] photo;

    public Item(Place place, String name) {
        this.place = place;
        this.room = null;
        this.name = name;
    }

    public Item(Room room, String name) {
        this.place = null;
        this.room = room;
        this.name = name;
    }
}
