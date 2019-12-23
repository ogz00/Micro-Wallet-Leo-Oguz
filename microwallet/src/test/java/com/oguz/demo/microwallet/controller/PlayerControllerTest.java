package com.oguz.demo.microwallet.controller;

import com.oguz.demo.microwallet.dto.PlayerDto;
import com.oguz.demo.microwallet.dto.TransactionDto;
import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.entity.Currency;
import com.oguz.demo.microwallet.entity.Player;
import com.oguz.demo.microwallet.entity.Transaction;
import com.oguz.demo.microwallet.entity.Wallet;
import com.oguz.demo.microwallet.helper.mapper.Mapper;
import com.oguz.demo.microwallet.service.player.PlayerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.oguz.demo.microwallet.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerControllerTest {
    @Mock
    private PlayerService playerService;

    @InjectMocks
    PlayerController controller;

    private List<WalletDto> walletDtoList = new ArrayList<>();
    private List<WalletDto> walletDtoListTransactions = new ArrayList<>();
    private PlayerDto playerDto;
    private BigDecimal balance = new BigDecimal(10);

    @Before
    public void before() {
        Currency currency = mock(Currency.class);
        when(currency.getId()).thenReturn(1);

        Player player = mock(Player.class);
        when(player.getId()).thenReturn(1L);
        when(player.getName()).thenReturn("name");
        when(player.getCountry()).thenReturn("country");
        playerDto = Mapper.from(player);

        Wallet wallet = mock(Wallet.class);
        when(wallet.getId()).thenReturn(1L);
        when(wallet.getPlayer()).thenReturn(player);
        when(wallet.getCurrency()).thenReturn(currency);

        Transaction transaction1;
        transaction1 = mock(Transaction.class);

        when(transaction1.getSuppliedId()).thenReturn(SUPPLIED_ID_1);
        when(transaction1.getAmount()).thenReturn(AMOUNT_1);

        when(transaction1.getWallet()).thenReturn(wallet);
        when(transaction1.getDescription()).thenReturn(DESCRIPTION);

        Set<Transaction> transactions = new HashSet<>();
        transactions.add(transaction1);

        List<TransactionDto> transactionDtoList = transactions.stream()
                .map(Mapper::from).collect(Collectors.toList());

        walletDtoList.add(Mapper.from(wallet, balance));
        walletDtoListTransactions.add(Mapper.from(wallet, transactionDtoList));
    }

    @Test
    public void testGetAllPlayers() {
        when(playerService.getAll()).thenReturn(Collections.singletonList(playerDto));
        ResponseEntity<List<PlayerDto>> response = controller.getAllPlayers();
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().size(), 1);
    }

    @Test
    public void testCreate_Success() {

        when(playerService.create(any(PlayerDto.class))).thenReturn(playerDto);
        ResponseEntity<PlayerDto> response = controller.createPlayer(playerDto);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().getId(), playerDto.getId());

    }

    @Test
    public void testGetWalletsBy_PlayerName_Success() {
        when(playerService.walletsByPlayerName(anyString())).thenReturn(walletDtoList);
        ResponseEntity<List<WalletDto>> response = controller.getWalletsByPlayerName(playerDto.getName());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().size(), walletDtoList.size());
        assertEquals(response.getBody().get(0).getId(), walletDtoList.get(0).getId());

    }

    @Test
    public void testGetWalletsBy_PlayerId_Success() {
        when(playerService.walletsByPlayerId(anyLong())).thenReturn(walletDtoList);
        ResponseEntity<List<WalletDto>> response = controller.getWalletsByPlayerId(playerDto.getId());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().size(), walletDtoList.size());
        assertEquals(response.getBody().get(0).getId(), walletDtoList.get(0).getId());
        assertEquals(response.getBody().get(0).getBalance(), walletDtoList.get(0).getBalance());

    }

    @Test
    public void testGetTransactionsBy_PlayerName_Success() {
        when(playerService.transactionsByPlayerName(anyString())).thenReturn(walletDtoListTransactions);

        ResponseEntity<List<WalletDto>> response = controller.getTransactionsByPlayerName(playerDto.getName());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().size(), walletDtoListTransactions.size());
        assertEquals(response.getBody().get(0).getTransactions(), walletDtoListTransactions.get(0).getTransactions());

    }

    @Test
    public void testGetTransactionsBy_PlayerId_Success() {
        when(playerService.transactionsByPlayerId(anyLong())).thenReturn(walletDtoListTransactions);

        ResponseEntity<List<WalletDto>> response = controller.getTransactionsByPlayerId(playerDto.getId());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().size(), walletDtoListTransactions.size());
        assertEquals(response.getBody().get(0).getTransactions(), walletDtoListTransactions.get(0).getTransactions());

    }
}
