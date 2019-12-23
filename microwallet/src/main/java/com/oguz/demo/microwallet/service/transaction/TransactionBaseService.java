package com.oguz.demo.microwallet.service.transaction;

import com.oguz.demo.microwallet.dto.TransactionDto;
import com.oguz.demo.microwallet.entity.Transaction;
import com.oguz.demo.microwallet.service.base.BaseService;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionBaseService extends BaseService<TransactionDto> {

    /**
     * @param suppliedId requested supplied id
     * @return Transaction Data Transfer Object
     */
    TransactionDto transactionBySuppliedId(@NotNull Long suppliedId);

    /**
     * @param walletId desired player id from RESTApi
     * @return
     */
    List<TransactionDto> transactionsByWalletId(@NotNull Long walletId);

    /**
     * @param walletId
     * @return This method returns last transaction for given wallet
     */
    Optional<Transaction> findTopTransaction(Long walletId);

    /**
     * @param walletId
     * @return This method returns the latest transaction for wallet.
     * With this approach, Tried to avoid from updating the table at every transaction request.
     * Because of the update wallet balance at every transaction process is more way costly then just insert a new line to transaction table,
     * Microwallet application calculate wallets balance with top transaction's @openingBalance and @amount fields.
     */
    BigDecimal calculateCurrentBalance(Long walletId);
}
