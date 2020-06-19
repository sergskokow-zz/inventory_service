package ru.gctc.inventory.server.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
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
    private Type type;

    @Column(nullable = false)
    private Integer number;

    private String description;

    public Container(Type type, int number) {
        this.type = type;
        this.number = number;
    }
    public Container(Type type, int number, String description) {
        this(type, number);
        this.description = description;
    }
}
