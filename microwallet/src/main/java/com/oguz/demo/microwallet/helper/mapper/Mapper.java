package com.oguz.demo.microwallet.helper.mapper;

import com.oguz.demo.microwallet.dto.CurrencyDto;
import com.oguz.demo.microwallet.dto.PlayerDto;
import com.oguz.demo.microwallet.dto.TransactionDto;
import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.entity.*;

import java.math.BigDecimal;
import java.util.List;

public final class Mapper {

    public static Transaction from(TransactionDto dto) {
        Integer transactionTypeId = dto.getTransactionType();
        Transaction transaction = new Transaction();
        BigDecimal amount = transactionTypeId
                .equals(TransactionType.DEBIT.value()) ? dto.getAmount().abs().negate() : dto.getAmount().abs();
        transaction.setAmount(amount);
        transaction.setDescription(dto.getDescription());
        transaction.setSuppliedId(dto.getSuppliedId());
        return transaction;
    }

    public static TransactionDto from(Transaction transaction) {
        return new TransactionDto.Builder()
                .setAmount(transaction.getAmount().abs())
                .setTransactionType(getTransactionType(transaction.getAmount()).value())
                .setSuppliedId(transaction.getSuppliedId())
                .setDescription(transaction.getDescription())
                .setWalletId(transaction.getWallet().getId())
                .build();
    }

    public static WalletDto from(Wallet w, BigDecimal balance) {
        return new WalletDto.Builder()
                .setCurrencyId(w.getCurrency().getId())
                .setPlayerId(w.getPlayer().getId())
                .setId(w.getId())
                .setBalance(balance)
                .build();
    }

    public static WalletDto from(Wallet w, List<TransactionDto> transactions) {
        return new WalletDto.Builder()
                .setCurrencyId(w.getCurrency().getId())
                .setPlayerId(w.getPlayer().getId())
                .setId(w.getId())
                .setTransactions(transactions)
                .build();
    }

    public static PlayerDto from(Player player) {
        PlayerDto dto = new PlayerDto();
        dto.setCountry(player.getCountry());
        dto.setName(player.getName());
        dto.setId(player.getId());
        return dto;
    }

    public static Player from(PlayerDto dto) {
        Player player = new Player();
        player.setCountry(dto.getCountry());
        player.setName(dto.getName());
        return player;
    }

    public static CurrencyDto from(Currency currency) {
        CurrencyDto dto = new CurrencyDto();
        dto.setCode(currency.getCode());
        dto.setName(currency.getName());
        dto.setId(currency.getId());
        return dto;
    }

    public static Currency from(CurrencyDto dto) {
        Currency currency = new Currency();
        currency.setCode(dto.getCode());
        currency.setName(dto.getName());
        return currency;
    }

    public static TransactionType getTransactionType(BigDecimal amount) {
        return amount.abs().equals(amount) ? TransactionType.CREDIT : TransactionType.DEBIT;
    }

    public static TransactionType getTransactionType(Integer typeId) {
        return typeId.equals(TransactionType.CREDIT.value()) ? TransactionType.CREDIT : TransactionType.DEBIT;
    }
}
