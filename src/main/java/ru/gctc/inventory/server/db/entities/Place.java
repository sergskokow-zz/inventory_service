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
@Table(name = "places")
@Getter
@Setter
@NoArgsConstructor
public class Place extends ContainsItems {
    @ManyToOne
    @JoinColumn(name = "container_id", nullable = false)
    @NotNull
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
    @NotNull
    private Type type;

    @Column(nullable = false)
    @NotNull
    private Integer number;

    private String name;

    public Place(Container container, Type type, int number) {
        this.container = container;
        this.type = type;
        this.number = number;
    }
    public Place(Container container, Type type, int number, String name) {
        this(container, type, number);
        this.name = name;
    }
}
