package com.oguz.demo.microwallet.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"}, callSuper = true)
@Entity(name = "player")
@NoArgsConstructor
public class Player extends BaseEntity implements UserDetails {
    private static final long serialVersionUID = 123L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Player fullName must be provided")
    @Column(nullable = false)
    private String fullName;

    @NotNull(message = "Player name must be provided")
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull(message = "Player password must be provided")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "Player country must be provided")
    @Column(nullable = false)
    private String country;

    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private Set<Wallet> wallets;


    public Player(String username, String country) {
        super();
        this.username = username;
        this.country = country;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + username + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("PLAYER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
