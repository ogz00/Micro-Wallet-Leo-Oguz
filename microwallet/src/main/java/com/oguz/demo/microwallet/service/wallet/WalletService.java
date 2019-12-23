package com.oguz.demo.microwallet.service.wallet;

import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.service.base.BaseService;

import java.util.List;

public interface WalletService extends BaseService<WalletDto> {

    List<WalletDto> walletsByPlayerIdAndCurrencyId(Long playerId, Integer currencyId);
    WalletDto transactionsByWalletId(Long id);

}
