package com.oguz.demo.microwallet.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"}, callSuper = true)
@Entity(name = "player")
@NoArgsConstructor
public class Player extends BaseEntity {
    private static final long serialVersionUID = 123L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Player name must be provided")
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull(message = "Player country must be provided")
    @Column(nullable = false)
    private String country;

    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private Set<Wallet> wallets;

    public Player(String name, String country) {
        super();
        this.name = name;
        this.country = country;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
