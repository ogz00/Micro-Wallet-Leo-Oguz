package com.oguz.demo.microwallet.service.currency;

import com.oguz.demo.microwallet.dto.CurrencyDto;
import com.oguz.demo.microwallet.entity.Currency;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.helper.mapper.Mapper;
import com.oguz.demo.microwallet.repository.CurrencyRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.oguz.demo.microwallet.exception.ErrorConstant.CURRENCY_NOT_FOUND;

@Service
@Transactional
@Log4j2
public class DefaultCurrencyService implements CurrencyService {

    private CurrencyRepository repository;

    @Autowired
    public DefaultCurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CurrencyDto> getAll() {
        return repository.findAll().stream().map(Mapper::from).collect(Collectors.toList());
    }

    @Override
    public CurrencyDto create(CurrencyDto currencyDto) throws MicroWalletException {
        Currency currency = Mapper.from(currencyDto);
        currency = repository.save(currency);
        return Mapper.from(currency);
    }

    @Override
    public CurrencyDto get(@NotNull Integer id) throws MicroWalletException {
        Currency currency = Optional.of(repository.findById(id)).get().orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(CURRENCY_NOT_FOUND, String.valueOf(id)));
        log.debug("Currency found with id=" + currency.getId());
        return Mapper.from(currency);
    }

    @Override
    public CurrencyDto get(@NotNull Long id) throws MicroWalletException {
        throw MicroWalletException.Builder.newInstance().setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .setMessage("Method doesnt support")
                .build();
    }
}
