package com.oguz.demo.microwallet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"}, callSuper = true)
@Entity(name = "wallet")
@NoArgsConstructor
public class Wallet extends BaseEntity {
    private static final long serialVersionUID = 123L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull(message = "Wallet player must be provided")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @NotNull(message = "Wallet currency must be provided")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    private Set<Transaction> transactions;

    public Wallet(@NotNull(message = "Wallet player must be provided") Player player,
                  @NotNull(message = "Wallet currency must be provided") Currency currency) {
        this.player = player;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", player=" + player +
                ", currency=" + currency +
                '}';
    }
}
