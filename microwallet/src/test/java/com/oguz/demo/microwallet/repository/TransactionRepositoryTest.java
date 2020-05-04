package com.oguz.demo.microwallet.repository;

import com.oguz.demo.microwallet.config.PersistenceContext;
import com.oguz.demo.microwallet.entity.*;
import com.oguz.demo.microwallet.helper.mapper.Mapper;
import com.oguz.demo.microwallet.helper.provider.AuditorAwareImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.oguz.demo.microwallet.TestConstants.*;
import static com.oguz.demo.microwallet.helper.provider.AuditorAwareImpl.TEST_USER;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = ASSIGNABLE_TYPE,
        classes = {AuditorAwareImpl.class, PersistenceContext.class}))
public class TransactionRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    TransactionRepository transactionRepository;


    private Wallet wallet;
    private Wallet wallet2;
    private Transaction transaction1;
    private Transaction transaction2;

    @Before
    public void before() {
        Currency currency = new Currency(CURRENCY_NAME, CURRENCY_CODE);
        entityManager.persistAndFlush(currency);
        Player player = new Player(PLAYER_NAME, PLAYER_COUNTRY);
        entityManager.persistAndFlush(player);
        wallet = new Wallet(player, currency);
        wallet2 = new Wallet(player, currency);
        entityManager.persist(wallet);
        entityManager.persistAndFlush(wallet2);


        transaction1 = new Transaction(SUPPLIED_ID_1, AMOUNT_1, OPENING_AMOUNT, wallet, DESCRIPTION);
        entityManager.persistAndFlush(transaction1);
        transaction2 = new Transaction(SUPPLIED_ID_2, AMOUNT_2.negate(), AMOUNT_1, wallet, DESCRIPTION);
        entityManager.persistAndFlush(transaction2);
    }


    @Test
    public void testFindBy_SuppliedId_Success() {
        Transaction transaction = transactionRepository.findBySuppliedId(SUPPLIED_ID_1).get();
        assertNotNull(transaction);

        assertEquals(transaction.getWallet().getId(), wallet.getId());
        assertEquals(Mapper.getTransactionType(transaction.getAmount()), TransactionType.CREDIT);
        assertEquals(transaction.getAmount(), AMOUNT_1);
        assertEquals(transaction.getSuppliedId(), SUPPLIED_ID_1);

    }

    @Test
    public void testFindAll_ByWallet_Success() {
        List<Transaction> transactions = transactionRepository.findAllByWallet(wallet);

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());
        assertEquals(2, transactions.size());
        assertEquals(transactions.get(0).getWallet().getId(), transactions.get(1).getWallet().getId());

        assertEquals(transactions.get(0).getWallet().getId(), wallet.getId());
        assertEquals(Mapper.getTransactionType(transactions.get(0).getAmount()), TransactionType.CREDIT);
        assertEquals(transactions.get(0).getAmount(), AMOUNT_1);
        assertEquals(transactions.get(0).getSuppliedId(), SUPPLIED_ID_1);
        assertNotNull(transactions.get(0).getCreatedAt());
        assertNotNull(transactions.get(0).getUpdatedAt());
        assertEquals(transactions.get(0).getUpdatedBy(), TEST_USER);
        assertEquals(transactions.get(0).getDescription(), DESCRIPTION);

        assertEquals(transactions.get(1).getWallet().getId(), wallet.getId());
        assertEquals(Mapper.getTransactionType(transactions.get(1).getAmount()), TransactionType.DEBIT);
        assertEquals(transactions.get(1).getAmount(), AMOUNT_2.negate());
        assertEquals(transactions.get(1).getSuppliedId(), SUPPLIED_ID_2);

        //Not Exist
        List<Transaction> transactionNotExist = transactionRepository.findAllByWallet(wallet2);

        assertNotNull(transactionNotExist);
        assertEquals(0, transactionNotExist.size());
    }

    @Test
    public void testFind_TopTransaction_Success() {
        Transaction transaction = transactionRepository.findTopTransaction(wallet.getId());
        assertNotNull(transaction);
        assertEquals(transaction, transaction2);

    }

    @Test
    public void testSaveTransaction_Success() {
        Transaction transaction = new Transaction(SUPPLIED_ID_2 +1, AMOUNT_1, OPENING_AMOUNT, wallet, DESCRIPTION);
        transaction = transactionRepository.save(transaction);
        assertNotNull(transaction);
        assertEquals(transaction.getWallet().getId(), wallet.getId());
        assertEquals(Mapper.getTransactionType(transaction.getAmount()), TransactionType.CREDIT);
        assertEquals(transaction.getAmount(), AMOUNT_1);
        assertEquals(transaction.getSuppliedId(), SUPPLIED_ID_2 + 1);
    }

    @Test
    public void testSaveTransaction_DuplicateSuppliedId_Failed() {
        try {
            Transaction transaction = new Transaction(SUPPLIED_ID_1, AMOUNT_1, OPENING_AMOUNT, wallet, DESCRIPTION);
            transactionRepository.save(transaction);
        }catch (DataIntegrityViolationException ex){
            assertNotNull(ex);
        }
    }
}
