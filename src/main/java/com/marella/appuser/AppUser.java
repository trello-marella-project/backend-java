package com.marella.appuser;

import com.marella.javaObjectClasses.Permission;
import com.marella.javaObjectClasses.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Site_user")
@Table(
        name = "site_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_email_unique", columnNames = "email"),
                @UniqueConstraint(name = "user_username_unique", columnNames = "username")
        }
)
public class AppUser implements UserDetails {
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
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "is_blocked", nullable = false)
    private Boolean blocked = false;

    @Column(name = "is_enabled", nullable = false)
    private Boolean enabled = false;

//    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private AppUserRole appUserRole;

    @OneToMany(
            mappedBy = "user",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<Space> spaces = new ArrayList<>();

    @OneToMany(
            mappedBy = "user",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<Permission> permissions = new ArrayList<>();

    public AppUser(String username, String password, String email, AppUserRole appUserRole) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.appUserRole = appUserRole;
        this.imageUrl = String.format("some_url/%s", username);
    }

    public void addSpace(Space space) {
        if (!this.spaces.contains(space)) {
            this.spaces.add(space);
            space.setUser(this);
        }
    }

    public void removeSpace(Space space) {
        if (this.spaces.contains(space)) {
            this.spaces.remove(space);
            space.setUser(null);
        }
    }

    public void addPermission(Permission permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + appUserRole +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(appUserRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !blocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
