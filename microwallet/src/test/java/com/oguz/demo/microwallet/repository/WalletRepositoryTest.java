package com.oguz.demo.microwallet.repository;

import com.oguz.demo.microwallet.config.PersistenceConfig;
import com.oguz.demo.microwallet.entity.Currency;
import com.oguz.demo.microwallet.entity.Player;
import com.oguz.demo.microwallet.entity.Wallet;
import com.oguz.demo.microwallet.helper.provider.AuditorAwareImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.oguz.demo.microwallet.TestConstants.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = ASSIGNABLE_TYPE,
        classes = {AuditorAwareImpl.class, PersistenceConfig.class}))
public class WalletRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    WalletRepository walletRepository;


    private Player player;
    private Currency currency;

    @Before
    public void before() {
        currency = new Currency(CURRENCY_NAME, CURRENCY_CODE);
        entityManager.persistAndFlush(currency);
        player = new Player(PLAYER_NAME, PLAYER_COUNTRY);
        entityManager.persistAndFlush(player);
        Wallet wallet = new Wallet(player, currency);
        Wallet wallet2 = new Wallet(player, currency);
        entityManager.persist(wallet);
        entityManager.persistAndFlush(wallet2);
//
//
//        Transaction transaction1 = new Transaction(SUPPLIED_ID_1, AMOUNT_1, OPENING_AMOUNT, wallet, DESCRIPTION);
//        entityManager.persistAndFlush(transaction1);
//        Transaction transaction2 = new Transaction(SUPPLIED_ID_2, AMOUNT_2.negate(), AMOUNT_1, wallet, DESCRIPTION);
//        entityManager.persistAndFlush(transaction2);
    }

    @Test
    public void testFindBy_Player_Success() {
        List<Wallet> wallets = walletRepository.findByPlayer(player);
        assertNotNull(wallets);
        assertEquals(wallets.size(), 2);

        assertEquals(wallets.get(0).getPlayer().getId(), player.getId());
        assertEquals(wallets.get(1).getPlayer().getId(), player.getId());

    }

    @Test
    public void testFindBy_Player_And_Currency_Success() {
        List<Wallet> wallets = walletRepository.findByPlayerAndCurrency(player, currency);
        assertNotNull(wallets);
        assertEquals(wallets.size(), 2);

        assertEquals(wallets.get(0).getPlayer().getId(), player.getId());
        assertEquals(wallets.get(0).getCurrency().getId(), currency.getId());
        assertEquals(wallets.get(1).getPlayer().getId(), player.getId());
        assertEquals(wallets.get(1).getCurrency().getId(), currency.getId());
    }
}
