package com.oguz.demo.microwallet.service.base;

import com.oguz.demo.microwallet.condition.JdbcTemplateCondition;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import org.springframework.context.annotation.Conditional;

import javax.validation.constraints.NotNull;

@Conditional(JdbcTemplateCondition.class)
public interface BaseService<T> {
    T create(T t) throws MicroWalletException;

    T get(@NotNull Long id) throws MicroWalletException;
}
