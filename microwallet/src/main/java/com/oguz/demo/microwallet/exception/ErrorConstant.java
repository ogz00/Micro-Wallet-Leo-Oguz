package com.oguz.demo.microwallet.exception;

public class ErrorConstant {
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL SERVER ERROR";
    public static final String PLAYER_NOT_FOUND = "Player %s not found.";
    public static final String CURRENCY_NOT_FOUND = "Currency %s not found.";
    public static final String WALLET_NOT_FOUND = "Wallet %s not found.";
    public static final String TRANSACTION_NOT_FOUND = "Transaction %s not found.";
    public static final String WALLET_BALANCE_NOT_FOUND = "Wallet %s balance request can not completed";
    public static final String INVALID_SUPPLIED_ID = "Unique index or primary key violation. Please check your request and try again.";
    public static final String NEGATIVE_TRANSACTION_AMOUNT = "Transaction amount should bigger than zero. Use transaction type 'Debit' for specify negative amount.";
    public static final String INSUFFICIENT_WALLET_BALANCE = "Insufficient Balance in Wallet %s to perform withdrawal transaction with amount %s";


    public static final String INVALID_NUMBER_FORMAT = "'%s' is an invalid number representation";
    public static final String VALIDATION_FAILED = "Object Validation Failed";
    public static final String INVALID_JSON = "Invalid/Malformed JSON request";

}
