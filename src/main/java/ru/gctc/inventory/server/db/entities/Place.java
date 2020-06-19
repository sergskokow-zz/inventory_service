package ru.gctc.inventory.server.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "places")
@Getter
@Setter
@NoArgsConstructor
public class Place extends ContainsItems {
    @ManyToOne
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("%s №%d %s", type==Type.SHELF?"Полка":"Позиция", number, name);
    }

    public enum Type { SHELF, POSITION }
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("SHELF")
    private Type type;

    @Column(nullable = false)
    private Integer number;

    private String name;

    public Place(Type type, int number) {
        this.type = type;
        this.number = number;
    }
    public Place(Type type, int number, String name) {
        this(type, number);
        this.name = name;
    }
}
