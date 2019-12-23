package com.oguz.demo.microwallet.service;

import com.oguz.demo.microwallet.dto.TransactionDto;
import com.oguz.demo.microwallet.entity.Transaction;
import com.oguz.demo.microwallet.entity.Wallet;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.helper.mapper.Mapper;
import com.oguz.demo.microwallet.repository.TransactionRepository;
import com.oguz.demo.microwallet.repository.WalletRepository;
import com.oguz.demo.microwallet.service.transaction.DefaultTransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;

import static com.oguz.demo.microwallet.TestConstants.*;
import static com.oguz.demo.microwallet.exception.ErrorConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultTransactionServiceTest {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    WalletRepository walletRepository;

    @InjectMocks
    DefaultTransactionService transactionService;


    private Wallet wallet;
    private Transaction transaction1;
    private Transaction transaction2;

    @Before
    public void before() {




        wallet = mock(Wallet.class);
        when(wallet.getId()).thenReturn(1L);

        Set<Wallet> wallets = new HashSet<>();
        wallets.add(wallet);




        transaction1 = mock(Transaction.class);
        when(transaction1.getId()).thenReturn(1L);
        when(transaction1.getSuppliedId()).thenReturn(SUPPLIED_ID_1);
        when(transaction1.getAmount()).thenReturn(AMOUNT_1);
        when(transaction1.getOpeningBalance()).thenReturn(OPENING_AMOUNT);
        when(transaction1.getWallet()).thenReturn(wallet);
        when(transaction1.getDescription()).thenReturn(DESCRIPTION);

        transaction2 = mock(Transaction.class);
        when(transaction2.getId()).thenReturn(2L);
        when(transaction2.getSuppliedId()).thenReturn(SUPPLIED_ID_2);
        when(transaction2.getAmount()).thenReturn(AMOUNT_2.negate());
        when(transaction2.getWallet()).thenReturn(wallet);
        when(transaction2.getDescription()).thenReturn(DESCRIPTION);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        when(transactionRepository.findAllByWallet(any(Wallet.class))).thenReturn(transactions);
    }

    @Test
    public void testCreateTransaction_Credit_Success() throws MicroWalletException {
        when(transactionRepository.findTopTransaction(anyLong())).thenReturn(null);
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(transaction1);
        TransactionDto dto = transactionService.create(Mapper.from(transaction1));
        assertNotNull(dto);
        assertEquals(dto.getWalletId(), transaction1.getWallet().getId());
        assertEquals(dto.getAmount(), transaction1.getAmount());

    }

    @Test
    public void testCreateTransaction_Debit_Success() throws MicroWalletException {
        when(transactionRepository.findTopTransaction(anyLong())).thenReturn(transaction1);
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(transaction2);
        TransactionDto dto = transactionService.create(Mapper.from(transaction2));

        assertNotNull(dto);
        assertEquals(dto.getWalletId(), transaction2.getWallet().getId());
        assertEquals(dto.getAmount().negate(), transaction2.getAmount());
    }

    @Test
    public void testCreateTransaction_Debit_Failed() throws MicroWalletException {
        when(transactionRepository.findTopTransaction(anyLong())).thenReturn(null);
        try {
            transactionService.create(Mapper.from(transaction2));
        } catch (MicroWalletException ex) {
            assertNotNull(ex);
            assertEquals(ex.getMessage(), String.format(
                    INSUFFICIENT_WALLET_BALANCE, transaction2.getWallet().getId(), transaction2.getAmount().negate()));
        }
    }

    @Test
    public void testGetTransactionBy_Id_Success() throws MicroWalletException {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(transaction1));
        TransactionDto dto = transactionService.get(1L);
        assertNotNull(dto);
    }

    @Test
    public void testGetTransactionBy_Id_Failed() throws MicroWalletException {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());
        try {
            transactionService.get(1L);
        } catch (MicroWalletException ex) {
            assertNotNull(ex);
            assertEquals(ex.getMessage(), String.format(TRANSACTION_NOT_FOUND, 1L));
            assertEquals(ex.getStatus(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testGetTransactionBy_SuppliedId_Success() throws MicroWalletException {
        when(transactionRepository.findBySuppliedId(anyLong())).thenReturn(Optional.of(transaction1));
        TransactionDto dto = transactionService.transactionBySuppliedId(1L);
        assertNotNull(dto);
    }

    @Test
    public void testGetTransactionBy_SuppliedId_Failed() throws MicroWalletException {
        when(transactionRepository.findBySuppliedId(anyLong())).thenReturn(Optional.empty());
        try {
            transactionService.transactionBySuppliedId(1L);
        } catch (MicroWalletException ex) {
            assertNotNull(ex);
            assertEquals(ex.getMessage(), String.format(TRANSACTION_NOT_FOUND, 1L));
            assertEquals(ex.getStatus(), HttpStatus.BAD_REQUEST);
        }
    }


    @Test
    public void testGetTransactions_ByWalletId_Success() throws MicroWalletException {
        List<TransactionDto> dtoList = transactionService.transactionsByWalletId(wallet.getId());
        assertEquals(dtoList.size(), 2);
        assertEquals(dtoList.get(0).getWalletId(), wallet.getId());
        assertEquals(dtoList.get(1).getWalletId(), wallet.getId());
    }

    @Test
    public void testGetTransactions_ByWalletId_Failed() throws MicroWalletException {
        try {
            transactionService.transactionsByWalletId(wallet.getId() + 1);
        } catch (MicroWalletException ex) {
            assertNotNull(ex);
            assertEquals(ex.getMessage(), String.format(WALLET_NOT_FOUND, wallet.getId() + 1));
            assertEquals(ex.getStatus(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testCalculateCurrentBalance_ExistTopTransaction() {
        when(transactionRepository.findTopTransaction(anyLong())).thenReturn(transaction1);
        BigDecimal balance = transactionService.calculateCurrentBalance(1L);
        assertNotNull(balance);
        assertEquals(balance, transaction1.getAmount().add(transaction1.getOpeningBalance()));
    }

    @Test
    public void testCalculateCurrentBalance_NotExistTopTransaction() {
        when(transactionRepository.findTopTransaction(anyLong())).thenReturn(null);
        BigDecimal balance = transactionService.calculateCurrentBalance(1L);
        assertNotNull(balance);
        assertEquals(balance, new BigDecimal(0));
    }

}
