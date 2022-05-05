package com.marella.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Entrance")
@Table(name = "entrance")
public class Entrance {
    @EmbeddedId
    private EntranceId id;

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

    @Column(nullable = false)
    private GregorianCalendar date;
}
