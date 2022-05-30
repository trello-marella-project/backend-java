package com.marella.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Report")
@Table(name = "report")
public class Report {
    @Id
    @SequenceGenerator(
            name = "report_sequence",
            sequenceName = "report_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "report_sequence"
    )
    Long id;

    @OneToOne
    @JoinColumn(
            name = "declarer_user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "declarer_user_id_fk"
            )
    )
    User declarerUser;

    @OneToOne
    @JoinColumn(
            name = "accused_user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "accused_user_id_fk"
            )
    )
    User accusedUser;

    @Column(nullable = false, columnDefinition = "TEXT")
    String message;

    public Report(User declarerUser, User accusedUser, String message) {
        this.declarerUser = declarerUser;
        this.accusedUser = accusedUser;
        this.message = message;
    }

    @Override
    public String toString() {
        return "{" +
                "\"report_id\":" + id +
                ",\"message\":\"" + message + '\"' +
                ",\"declarerUser\":" +
                String.format("{\"user_id\":%d" +
                                ",\"email\":\"%s\"" +
                                ",\"username\":\"%s\"}",
                        declarerUser.getId(), declarerUser.getEmail(), declarerUser.getUsername()) +
                ",\"accusedUser\":"  +
                String.format("{\"user_id\":%d" +
                        ",\"email\":\"%s\"" +
                        ",\"username\":\"%s\"}",
                        accusedUser.getId(), accusedUser.getEmail(), accusedUser.getUsername()) +
                "}";
    }
}
