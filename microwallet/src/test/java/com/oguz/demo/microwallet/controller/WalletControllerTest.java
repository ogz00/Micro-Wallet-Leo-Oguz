package com.oguz.demo.microwallet.controller;

import com.oguz.demo.microwallet.dto.CurrencyDto;
import com.oguz.demo.microwallet.dto.PlayerDto;
import com.oguz.demo.microwallet.dto.TransactionDto;
import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.service.wallet.WalletService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.oguz.demo.microwallet.TestConstants.*;
import static com.oguz.demo.microwallet.controller.IntegrationTestUtil.APPLICATION_JSON_UTF8;
import static com.oguz.demo.microwallet.controller.IntegrationTestUtil.convertObjectToJsonBytes;
import static com.oguz.demo.microwallet.exception.ErrorConstant.WALLET_NOT_FOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = WalletController.class)
public class WalletControllerTest {
    @MockBean
    private WalletService walletService;

    @Autowired
    private MockMvc mockMvc;

    private WalletDto walletDto;

    private List<TransactionDto> transactionDtos = new ArrayList<>();

    @Before
    public void before() {
        CurrencyDto currencyDto = new CurrencyDto(1, CURRENCY_NAME, CURRENCY_CODE);
        PlayerDto playerDto = new PlayerDto(1L, PLAYER_NAME, PLAYER_USERNAME, PLAYER_PASSWORD, PLAYER_COUNTRY);

        walletDto = new WalletDto.Builder().setCurrencyId(currencyDto.getId()).setId(1L)
                .setPlayerId(playerDto.getId())
                .setBalance(AMOUNT_1.add(AMOUNT_2.negate()).setScale(2, RoundingMode.HALF_UP))
                .build();

        TransactionDto transactionDtoCredit;
        TransactionDto transactionDtoDebit;

        transactionDtoCredit = new TransactionDto.Builder().setTransactionType(1).setAmount(AMOUNT_1).setSuppliedId(SUPPLIED_ID_1)
                .setWalletId(walletDto.getId()).setDescription(DESCRIPTION).build();

        transactionDtoDebit = new TransactionDto.Builder().setTransactionType(2).setAmount(AMOUNT_2).setSuppliedId(SUPPLIED_ID_2)
                .setWalletId(walletDto.getId()).setDescription(DESCRIPTION_DEBIT).build();
        transactionDtos.add(transactionDtoCredit);
        transactionDtos.add(transactionDtoDebit);
        walletDto.setTransactions(transactionDtos);
    }

    @Test
    public void testGetWalletById_Success() throws Exception {
        given(walletService.get(anyLong())).willReturn(walletDto);
        mockMvc.perform(get("/wallet/{id}", 1L)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(walletDto.getId().intValue())))
                .andExpect(jsonPath("$.currencyId", is(walletDto.getCurrencyId())))
                .andExpect(jsonPath("$.playerId", is(walletDto.getPlayerId().intValue())));
    }

    @Test
    public void testGetWalletById_Failed() throws Exception {

        given(walletService.get(anyLong())).willThrow(MicroWalletException.Builder.newInstance().buildWithStringFormatter(WALLET_NOT_FOUND, String.valueOf(2L)));

        mockMvc.perform(get("/wallet/{id}", 2L)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(String.format(WALLET_NOT_FOUND, String.valueOf(2L)))));
    }

    @Test
    public void testGetWalletBalance_Success() throws Exception {
        given(walletService.get(anyLong())).willReturn(walletDto);
        mockMvc.perform(get("/wallet/{id}/balance", 1L)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateWallet_Success() throws Exception {
        given(walletService.create(any(WalletDto.class))).willReturn(walletDto);

        mockMvc.perform(post("/wallet")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(walletDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId", is(walletDto.getPlayerId().intValue())))
                .andExpect(jsonPath("$.currencyId", is(walletDto.getCurrencyId())))
                .andExpect(jsonPath("$.id", is(walletDto.getId().intValue())));
    }

    @Test
    public void testCreateWallet__NoCurrency_Failed() throws Exception {
        WalletDto dto = new WalletDto.Builder().setPlayerId(1L).setId(1L).build();
        mockMvc.perform(post("/wallet")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(dto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Object Validation Failed")));

    }

    @Test
    public void testGetWalletTransactions_Success() throws Exception {
        given(walletService.transactionsByWalletId(anyLong())).willReturn(walletDto);
        mockMvc.perform(get("/wallet/{id}/transactions", 1L)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions[0].suppliedId").value(walletDto.getTransactions().get(0).getSuppliedId()))
                .andExpect(jsonPath("$.transactions[1].suppliedId").value(walletDto.getTransactions().get(1).getSuppliedId()));
    }


}
