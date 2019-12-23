package com.oguz.demo.microwallet.repository;

import com.oguz.demo.microwallet.entity.Currency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static com.oguz.demo.microwallet.TestConstants.CURRENCY_CODE;
import static com.oguz.demo.microwallet.TestConstants.CURRENCY_NAME;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CurrencyRepositoryTest {
    public static final Integer _ID = 1;
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CurrencyRepository currencyRepository;

    private Currency currency;

    @Before
    public void before() {
        currency = new Currency(CURRENCY_NAME, CURRENCY_CODE);
        entityManager.persistAndFlush(currency);
    }

    @Test
    public void testFindByCode() {
        Currency currency = currencyRepository.findById(_ID).get();

       assertNotNull(currency);
       assertEquals(currency.getName(), CURRENCY_NAME);
       assertEquals(currency.getCode(), CURRENCY_CODE);
    }
}
