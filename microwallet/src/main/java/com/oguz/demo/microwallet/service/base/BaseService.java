package com.oguz.demo.microwallet.service.base;

import com.oguz.demo.microwallet.exception.MicroWalletException;

import javax.validation.constraints.NotNull;

public interface BaseService<T> {
    T create(T t) throws MicroWalletException;
    T get(@NotNull Long id) throws MicroWalletException;
}
