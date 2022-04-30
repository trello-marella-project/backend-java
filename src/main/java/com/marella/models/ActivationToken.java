package com.marella.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "Activation_token")
@Table(name = "activation_token", uniqueConstraints = @UniqueConstraint(name = "token_unique", columnNames = "token"))
public class ActivationToken {
    @Id
    @SequenceGenerator(
            name = "activation_token_sequence",
            sequenceName = "activation_token_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "activation_token_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "user_id_fk"
            )
    )
    private User user;

    public ActivationToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
