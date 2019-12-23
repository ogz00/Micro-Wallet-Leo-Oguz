package com.oguz.demo.microwallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletDto implements Serializable {
    private static final long serialVersionUID = 1234L;

    private Long id;
    @NotNull
    private Long playerId;
    @NotNull
    private Integer currencyId;
    @Digits(integer = 11, fraction = 2)
    private BigDecimal balance;
    private List<TransactionDto> transactions;

    public WalletDto() {
    }

    private WalletDto(Builder builder) {
        this.id = builder.id;
        this.playerId = builder.playerId;
        this.currencyId = builder.currencyId;
        this.balance = builder.balance;
        this.transactions = builder.transactions;
    }

    public static class Builder {
        private Long id;
        private Long playerId;
        private Integer currencyId;
        private BigDecimal balance;
        private List<TransactionDto> transactions;

        public Builder() {

        }

        public Builder setPlayerId(Long playerId) {
            this.playerId = playerId;
            return this;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setCurrencyId(Integer id) {
            this.currencyId = id;
            return this;
        }

        public Builder setBalance(BigDecimal decimal) {
            this.balance = decimal;
            return this;
        }

        public Builder setTransactions(List<TransactionDto> transactions) {
            this.transactions = transactions;
            return this;
        }

        public WalletDto build() {
            return new WalletDto(this);
        }

    }
}
