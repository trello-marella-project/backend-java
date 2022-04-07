package com.marella.javaObjectClasses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "Block")
@Table(name = "blocks")
public class Block {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(
            name = "space_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "space_id_fk"
            )
    )
    private Space space;

    @OneToMany(
            mappedBy = "block",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Card> cards = new ArrayList<>();

    public Block(String name) {
        this.name = name;
    }

    public void addCard(Card card){
        if(!cards.contains(card)){
            cards.add(card);
            card.setBlock(this);
        }
    }

    public void removeCard(Card card){
        if(cards.contains(card)){
            cards.remove(card);
            card.setBlock(null);
        }
    }

    @Override
    public String toString() {
        return "Block{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", space=" + space +
                '}';
    }
}
