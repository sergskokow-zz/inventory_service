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

    public Place(Container container) {
        this.container = container;
    }
}
