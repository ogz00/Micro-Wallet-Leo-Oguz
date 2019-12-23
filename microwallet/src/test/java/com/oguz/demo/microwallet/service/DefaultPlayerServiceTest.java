package com.oguz.demo.microwallet.service;

import com.oguz.demo.microwallet.dto.PlayerDto;
import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.entity.Currency;
import com.oguz.demo.microwallet.entity.Player;
import com.oguz.demo.microwallet.entity.Transaction;
import com.oguz.demo.microwallet.entity.Wallet;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.helper.mapper.Mapper;
import com.oguz.demo.microwallet.repository.PlayerRepository;
import com.oguz.demo.microwallet.repository.WalletRepository;
import com.oguz.demo.microwallet.service.player.DefaultPlayerService;
import com.oguz.demo.microwallet.service.transaction.TransactionBaseService;
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
import static com.oguz.demo.microwallet.exception.ErrorConstant.PLAYER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionBaseService transactionService;

    @InjectMocks
    private DefaultPlayerService playerService;

    private Wallet wallet;
    private Player player;
    private Set<Transaction> transactions = new HashSet<>();

    @Before
    public void before() {
        Currency currency = mock(Currency.class);
        when(currency.getId()).thenReturn(1);

        player = mock(Player.class);
        when(player.getId()).thenReturn(1L);
        when(player.getName()).thenReturn("name");
        when(player.getCountry()).thenReturn("country");

        wallet = mock(Wallet.class);
        when(wallet.getId()).thenReturn(1L);

        when(wallet.getPlayer()).thenReturn(player);
        when(wallet.getCurrency()).thenReturn(currency);
        Transaction transaction1;
        Transaction transaction2;
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

    }

    @Test
    public void testCreate_Success() {
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        PlayerDto dto = playerService.create(Mapper.from(player));
        assertNotNull(dto);
        assertEquals(dto.getCountry(), player.getCountry());
        assertEquals(dto.getName(), player.getName());
    }

    @Test
    public void testGetBy_Id_Success() {
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        PlayerDto dto = playerService.get(1L);
        assertNotNull(dto);
        assertEquals(dto.getCountry(), player.getCountry());
        assertEquals(dto.getName(), player.getName());
        assertEquals(dto.getId(), player.getId());
    }

    @Test
    public void testTransactionsBy_PlayerName_Success() {
        when(playerRepository.findByName(anyString())).thenReturn(player);
        when(walletRepository.findByPlayer(any(Player.class))).thenReturn(Collections.singletonList(wallet));
        when(wallet.getTransactions()).thenReturn(transactions);
        List<WalletDto> walletDtoList = playerService.transactionsByPlayerName(player.getName());
        assertNotNull(walletDtoList);
        assertEquals(walletDtoList.size(), 1);
        assertEquals(walletDtoList.get(0).getPlayerId(), player.getId());
        assertEquals(walletDtoList.get(0).getCurrencyId(), wallet.getCurrency().getId());
        assertEquals(walletDtoList.get(0).getTransactions().size(), 2);
    }

    @Test
    public void testGetTransactionsBy_PlayerName_Failed() throws MicroWalletException {
        when(playerRepository.findByName(anyString())).thenReturn(null);
        try {
            playerService.transactionsByPlayerName(player.getName());
        } catch (MicroWalletException ex) {
            assertNotNull(ex);
            assertEquals(ex.getMessage(), String.format(PLAYER_NOT_FOUND, player.getName()));
            assertEquals(ex.getStatus(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testTransactionsBy_PlayerId_Success() {
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(walletRepository.findByPlayer(any(Player.class))).thenReturn(Collections.singletonList(wallet));
        when(wallet.getTransactions()).thenReturn(transactions);
        List<WalletDto> walletDtoList = playerService.transactionsByPlayerId(player.getId());
        assertNotNull(walletDtoList);
        assertEquals(walletDtoList.size(), 1);
        assertEquals(walletDtoList.get(0).getPlayerId(), player.getId());
        assertEquals(walletDtoList.get(0).getCurrencyId(), wallet.getCurrency().getId());
        assertEquals(walletDtoList.get(0).getTransactions().size(), 2);
    }

    @Test
    public void testGetTransactionsBy_PlayerId_Failed() throws MicroWalletException {
        when(playerRepository.findById(anyLong())).thenReturn(Optional.empty());
        try {
            playerService.transactionsByPlayerId(player.getId());
        } catch (MicroWalletException ex) {
            assertNotNull(ex);
            assertEquals(ex.getMessage(), String.format(PLAYER_NOT_FOUND, player.getId()));
            assertEquals(ex.getStatus(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testWalletsBy_PlayerName_Success() {
        when(playerRepository.findByName(anyString())).thenReturn(player);
        when(walletRepository.findByPlayer(any(Player.class))).thenReturn(Collections.singletonList(wallet));
        when(transactionService.calculateCurrentBalance(anyLong())).thenReturn(new BigDecimal(10));
        List<WalletDto> walletDtoList = playerService.walletsByPlayerName(player.getName());
        assertNotNull(walletDtoList);
        assertEquals(walletDtoList.size(), 1);
        assertEquals(walletDtoList.get(0).getPlayerId(), player.getId());
        assertEquals(walletDtoList.get(0).getCurrencyId(), wallet.getCurrency().getId());
        assertEquals(walletDtoList.get(0).getBalance(), new BigDecimal(10));
    }


    @Test
    public void testWalletsBy_PlayerId_Success() {
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(walletRepository.findByPlayer(any(Player.class))).thenReturn(Collections.singletonList(wallet));
        when(transactionService.calculateCurrentBalance(anyLong())).thenReturn(new BigDecimal(10));
        List<WalletDto> walletDtoList = playerService.walletsByPlayerId(player.getId());
        assertNotNull(walletDtoList);
        assertEquals(walletDtoList.size(), 1);
        assertEquals(walletDtoList.get(0).getPlayerId(), player.getId());
        assertEquals(walletDtoList.get(0).getCurrencyId(), wallet.getCurrency().getId());
        assertEquals(walletDtoList.get(0).getBalance(), new BigDecimal(10));
    }

    @Test
    public void testGetAllPlayer() {
        when(playerRepository.findAll()).thenReturn(Collections.singletonList(player));
        List<PlayerDto> dtoList = playerService.getAll();
        assertNotNull(dtoList);
        assertEquals(dtoList.size(), 1);
        assertEquals(dtoList.get(0).getId(), player.getId());

    }

}
