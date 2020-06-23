package ru.gctc.inventory.server.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "containers")
@Getter
@Setter
@NoArgsConstructor
public class Container extends InventoryEntity {
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @NotNull
    private Room room;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL)
    private List<Place> places = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("%s №%d %s", type==Type.CASE?"Шкаф":"Стеллаж", number, description);
    }

    public enum Type { CASE, RACK }
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("CASE")
    @NotNull
    private Type type;

    @Column(nullable = false)
    @NotNull
    private Integer number;

    private String description;

    public Container(Room room, Type type, int number) {
        this.room = room;
        this.type = type;
        this.number = number;
    }
    public Container(Room room, Type type, int number, String description) {
        this(room, type, number);
        this.description = description;
    }
}
