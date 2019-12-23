package com.oguz.demo.microwallet.service.currency;

import com.oguz.demo.microwallet.dto.CurrencyDto;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.service.base.BaseService;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface CurrencyService extends BaseService<CurrencyDto> {
    List<CurrencyDto> getAll();
    CurrencyDto get(@NotNull Integer id) throws MicroWalletException;
}
