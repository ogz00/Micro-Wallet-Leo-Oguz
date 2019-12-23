package com.oguz.demo.microwallet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = {"id"}, callSuper = true)
@Entity(name = "transaction")
@NoArgsConstructor
public class Transaction extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull(message = "Transaction supplied id must be provided")
    @Column(name = "supplied_id", unique = true, nullable = false)
    private Long suppliedId;

    @NotNull(message = "Transaction amount must be provided")
    @Column(name = "amount", nullable = false)
    @Digits(integer = 11, fraction = 2)
    private BigDecimal amount;

    @NotNull(message = "Transaction opening balance must be provided")
    @Column(name = "opening_balance", nullable = false)
    @Min(0)
    private BigDecimal openingBalance;

    @NotNull(message = "Transaction wallet must be provided")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Column(name = "description", columnDefinition = "Varchar(255)")
    String description;

    public Transaction(@NotNull(message = "Transaction supplied id must be provided") Long suppliedId,
                       @NotNull(message = "Transaction amount must be provided") BigDecimal amount,
                       @NotNull(message = "Transaction opening balance must be provided") BigDecimal openingBalance,
                       @NotNull(message = "Transaction wallet must be provided") Wallet wallet,
                       String description) {
        this.suppliedId = suppliedId;
        this.amount = amount;
        this.openingBalance = openingBalance;
        this.wallet = wallet;
        this.description = description;
    }

}
