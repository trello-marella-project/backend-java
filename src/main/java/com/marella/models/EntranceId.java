package com.marella.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class EntranceId implements Serializable {
    @Column(name = "user_id")
    private Long UserId;

    @Column(name = "space_id")
    private Long SpaceId;

    public EntranceId(Long userId, Long spaceId) {
        UserId = userId;
        SpaceId = spaceId;
    }

    @Override
    public String toString() {
        return "PermissionLogId{" +
                "UserId=" + UserId +
                ", SpaceId=" + SpaceId +
                '}';
    }
}
