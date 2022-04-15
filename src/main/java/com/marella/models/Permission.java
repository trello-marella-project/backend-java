package com.marella.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Permission")
@Table(name = "permission")
public class Permission {
    @EmbeddedId
    private PermissionId id;

    @ManyToOne
    @MapsId("UserId")
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(
                    name = "enrolment_user_id_fk"
            )
    )
    private User user;

    @ManyToOne
    @MapsId("SpaceId")
    @JoinColumn(
            name = "space_id",
            foreignKey = @ForeignKey(
                    name = "enrolment_space_id_fk"
            )
    )
    private Space space;

    public Permission(User user, Space space) {
        this.id = new PermissionId(user.getId(), space.getId());
        this.user = user;
        this.space = space;
    }

    public Permission(PermissionId id, User user, Space space) {
        this.id = id;
        this.user = user;
        this.space = space;
    }

//    Удалить
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", user=" + user +
                ", space=" + space +
                '}';
    }
}
