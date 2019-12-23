package com.oguz.demo.microwallet.service;

import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.entity.Currency;
import com.oguz.demo.microwallet.entity.Player;
import com.oguz.demo.microwallet.entity.Transaction;
import com.oguz.demo.microwallet.entity.Wallet;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.helper.mapper.Mapper;
import com.oguz.demo.microwallet.repository.CurrencyRepository;
import com.oguz.demo.microwallet.repository.PlayerRepository;
import com.oguz.demo.microwallet.repository.WalletRepository;
import com.oguz.demo.microwallet.service.transaction.DefaultTransactionService;
import com.oguz.demo.microwallet.service.wallet.DefaultWalletService;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultWalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private DefaultTransactionService transactionService;

    @InjectMocks
    private DefaultWalletService walletService;

    private Wallet wallet;
    private Player player;
    private Currency currency;
    private Transaction transaction1;
    private Transaction transaction2;
    private Set<Transaction> transactions = new HashSet<>();

    @Before
    public void before() {
        currency = mock(Currency.class);
        when(currency.getId()).thenReturn(1);

        player = mock(Player.class);
        when(player.getId()).thenReturn(1L);

        wallet = mock(Wallet.class);
        when(wallet.getId()).thenReturn(1L);

        when(wallet.getPlayer()).thenReturn(player);
        when(wallet.getCurrency()).thenReturn(currency);

        transaction1 = mock(Transaction.class);

        when(transaction1.getSuppliedId()).thenReturn(SUPPLIED_ID_1);
        when(transaction1.getAmount()).thenReturn(AMOUNT_1);

        when(transaction1.getWallet()).thenReturn(wallet);
        when(transaction1.getDescription()).thenReturn(DESCRIPTION);

        transaction2 = mock(Transaction.class);

        when(transaction2.getSuppliedId()).thenReturn(SUPPLIED_ID_2);
        when(transaction2.getAmount()).thenReturn(AMOUNT_2.negate());

        when(transaction2.getWallet()).thenReturn(wallet);
        when(transaction2.getDescription()).thenReturn(DESCRIPTION);

        transactions.add(transaction1);
        transactions.add(transaction2);

        when(wallet.getTransactions()).thenReturn(transactions);


    }

    @Test
    public void testCreateWallet_Success() {
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(currencyRepository.findById(anyInt())).thenReturn(Optional.of(currency));
        when(walletRepository.saveAndFlush(any(Wallet.class))).thenReturn(wallet);

        WalletDto dto = walletService.create(Mapper.from(wallet, new BigDecimal(0)));
        assertNotNull(dto);
        assertEquals(dto.getCurrencyId(), currency.getId());
        assertEquals(dto.getPlayerId(), player.getId());
        assertEquals(dto.getId(), wallet.getId());
        assertEquals(dto.getBalance(), new BigDecimal(0));
    }

    @Test
    public void testCreateWallet_Failed() {
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(currencyRepository.findById(anyInt())).thenReturn(Optional.empty());
        try {
            walletService.create(Mapper.from(wallet, new BigDecimal(0)));
        } catch (MicroWalletException ex) {
            assertNotNull(ex);
            assertNotNull(ex.getRootException());
            assertEquals(ex.getMessage(), INTERNAL_SERVER_ERROR);
            assertEquals(ex.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
            assertEquals(ex.getRootException().getMessage(), String.format(CURRENCY_NOT_FOUND, wallet.getCurrency().getId()));
            assertEquals(((MicroWalletException) ex.getRootException()).getStatus(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testGetWalletBy_Id_Success() {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(wallet));
        when(transactionService.calculateCurrentBalance(anyLong())).thenReturn(new BigDecimal(10));
        WalletDto dto = walletService.get(1L);
        assertNotNull(dto);
        assertEquals(dto.getId(), wallet.getId());
        assertEquals(dto.getPlayerId(), wallet.getPlayer().getId());
        assertEquals(dto.getCurrencyId(), wallet.getCurrency().getId());
        assertEquals(dto.getBalance(), new BigDecimal(10));
    }

    @Test
    public void testGetWalletBy_Id_Failed() throws MicroWalletException {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.empty());
        try {
            walletService.get(1L);
        } catch (MicroWalletException ex) {
            assertNotNull(ex);
            assertEquals(ex.getMessage(), String.format(WALLET_NOT_FOUND, 1L));
            assertEquals(ex.getStatus(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testFindWalletsBy_PlayerId_And_CurrencyId_Success() {
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(currencyRepository.findById(anyInt())).thenReturn(Optional.of(currency));
        when(transactionService.calculateCurrentBalance(anyLong())).thenReturn(new BigDecimal(10));
        when(walletRepository.findByPlayerAndCurrency(any(Player.class), any(Currency.class))).thenReturn(Collections.singletonList(wallet));
        List<WalletDto> dtoList = walletService.walletsByPlayerIdAndCurrencyId(1L, 1);
        assertNotNull(dtoList);
        assertEquals(dtoList.size(), 1);
        assertEquals(dtoList.get(0).getBalance(), new BigDecimal(10));
        assertNull(dtoList.get(0).getTransactions());

    }

    @Test
    public void testFindWalletsBy_PlayerId_And_CurrencyId_Success_EmptyList() throws MicroWalletException {
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(currencyRepository.findById(anyInt())).thenReturn(Optional.of(currency));
        when(walletRepository.findByPlayerAndCurrency(any(Player.class), any(Currency.class))).thenReturn(Collections.emptyList());
        List<WalletDto> dtoList = walletService.walletsByPlayerIdAndCurrencyId(1L, 1);
        assertNotNull(dtoList);
        assertEquals(dtoList.size(), 0);
    }

    @Test
    public void testFindWalletsBy_PlayerId_And_CurrencyId_Failed() throws MicroWalletException {
        when(playerRepository.findById(anyLong())).thenReturn(Optional.empty());
        try {
            walletService.walletsByPlayerIdAndCurrencyId(1L, 1);
        } catch (MicroWalletException ex) {
            assertNotNull(ex);
            assertEquals(ex.getMessage(), String.format(PLAYER_NOT_FOUND, 1L));
            assertEquals(ex.getStatus(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testFindTransactionsBy_WalletId_Success() {
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(wallet));
        WalletDto walletDto = walletService.transactionsByWalletId(wallet.getId());
        assertNotNull(walletDto);
        assertNotNull(walletDto.getTransactions());
        assertEquals(walletDto.getTransactions().size(), 2);

    }

}
