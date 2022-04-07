package com.marella.javaObjectClasses;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Tag")
@Table(name = "tag_log")
public class Tag {
    @Id
    @SequenceGenerator(
            name = "tag_sequence",
            sequenceName = "tag_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "tag_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String tag;

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

    public Tag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                ", space=" + space +
                '}';
    }
}
