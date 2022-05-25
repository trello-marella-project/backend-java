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
            fetch = FetchType.LAZY
    )
    private List<Entrance> entrances = new ArrayList<>();

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

    public void addEntrance(Entrance entrance) {
        if (!entrances.contains(entrance)) {
            entrances.add(entrance);
        }
    }

    public void removeEntrance(Entrance entrance) {
        entrances.remove(entrance);
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

    public void addBlock(Block block){
        if(!blocks.contains(block)){
            blocks.add(block);
            block.setSpace(this);
        }
    }

    public void removeBlock(Block block){
        if(blocks.contains(block)){
            blocks.remove(block);
            block.setSpace(null);
        }
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();
        response.append(String.format("{\"space_id\":%d,", id));
        response.append(String.format("\"is_public\":%s,", isPublic));
        response.append(String.format("\"name\":\"%s\",", name));

        String prefix = "\"members\":[";
        if (permissions.isEmpty()) response.append(prefix);
        else
            for (Permission permission : permissions) {
                response.append(prefix);
                response.append(String.format("\"%s\"", permission.getUser().getUsername()));
                prefix = ",";
            }
        response.append("],");

        prefix = "\"blocks\":[";
        if (blocks.isEmpty()) response.append(prefix);
        else
            for (Block block : blocks) {
                response.append(prefix);
                response.append(String.format("{\"block_id\":%d,", block.getId()));
                response.append(String.format("\"name\":\"%s\",", block.getName()));
                String cardPrefix = "\"cards\":[";
                if(block.getCards().isEmpty()) response.append(cardPrefix);
                else
                    for(Card card : block.getCards()){
                        response.append(cardPrefix);
                        response.append(String.format("{\"card_id\":\"%s\",", card.getId()));
                        response.append(String.format("\"name\":\"%s\",", card.getName()));
                        response.append(String.format("\"description\":\"%s\"}", card.getDescription()));
                        cardPrefix = ",";
                    }
                response.append("]}");
                prefix = ",";
            }
        response.append("]}");
        return response.toString();
    }
}
