package com.oguz.demo.microwallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;

import static com.oguz.demo.microwallet.exception.ErrorConstant.NEGATIVE_TRANSACTION_AMOUNT;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto implements Serializable {
    private static final long serialVersionUID = 1234L;

    @NotNull
    private Long suppliedId;
    @NotNull
    @Positive(message = NEGATIVE_TRANSACTION_AMOUNT)
    @Digits(integer = 11, fraction = 2)
    private BigDecimal amount;
    @NotNull
    private Long walletId;
    @NotNull
    private Integer transactionType;
    private String description;

    public TransactionDto() {
    }

    public TransactionDto(Builder builder) {
        this.amount = builder.amount;
        this.suppliedId = builder.suppliedId;
        this.walletId = builder.walletId;
        this.transactionType = builder.transactionType;
        this.description = builder.description;
    }

    public static class Builder {
        private Long suppliedId;
        private BigDecimal amount;
        private Long walletId;
        private Integer transactionType;
        private String description;


        public Builder setSuppliedId(Long id) {
            this.suppliedId = id;
            return this;
        }

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder setWalletId(Long id) {
            this.walletId = id;
            return this;
        }

        public Builder setTransactionType(Integer type) {
            this.transactionType = type;
            return this;
        }

        public Builder setDescription(String str) {
            this.description = str;
            return this;
        }

        public TransactionDto build() {
            return new TransactionDto(this);
        }

    }

}
