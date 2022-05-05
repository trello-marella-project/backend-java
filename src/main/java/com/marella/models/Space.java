package com.marella.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Space")
@Table(name = "spaces")
public class Space {
    @Id
    @SequenceGenerator(
            name = "space_sequence",
            sequenceName = "space_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "space_sequence"
    )
    private Long id;

    @Column(
            nullable = false
    )
    private String name;

    @Column(
            nullable = false
    )
    private boolean isPublic;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "user_id_fk"
            )
    )
    private User user;

    @OneToMany(
            mappedBy = "space",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<Permission> permissions = new ArrayList<>();

    @OneToMany(
            mappedBy = "space",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<Permission> entrances = new ArrayList<>();

    @OneToMany(
            mappedBy = "space",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(
            mappedBy = "space",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Block> blocks = new ArrayList<>();

    public Space(String name, boolean isPublic) {
        this.name = name;
        this.isPublic = isPublic;
    }

    public void addPermission(Permission permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }

    public void addTag(Tag tag){
        if(!tags.contains(tag)){
            tags.add(tag);
            tag.setSpace(this);
        }
    }

    public void removeTag(Tag tag){
        if(tags.contains(tag)){
            tags.remove(tag);
            tag.setSpace(null);
        }
    }

    public void addBlocks(Block block){
        if(!blocks.contains(block)){
            blocks.add(block);
            block.setSpace(this);
        }
    }

    public void removeBlocks(Block block){
        if(blocks.contains(block)){
            blocks.remove(block);
            block.setSpace(null);
        }
    }

    @Override
    public String toString() {
        return "Space{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isPublic=" + isPublic +
                ", user=" + user +
                '}';
    }
}
