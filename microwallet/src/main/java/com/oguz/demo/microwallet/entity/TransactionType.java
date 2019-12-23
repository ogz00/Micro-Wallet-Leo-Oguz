package com.oguz.demo.microwallet.entity;

import java.io.Serializable;

public enum TransactionType implements Serializable {

    CREDIT(1),

    DEBIT(2);

    private final int transactionTypeId;

    TransactionType(Integer transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public Integer value() {
        return this.transactionTypeId;
    }

}
