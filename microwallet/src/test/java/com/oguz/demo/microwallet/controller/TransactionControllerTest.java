package com.oguz.demo.microwallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oguz.demo.microwallet.dto.TransactionDto;
import com.oguz.demo.microwallet.service.transaction.TransactionBaseService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.oguz.demo.microwallet.TestConstants.*;
import static com.oguz.demo.microwallet.controller.IntegrationTestUtil.APPLICATION_JSON_UTF8;
import static com.oguz.demo.microwallet.controller.IntegrationTestUtil.convertObjectToJsonBytes;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TransactionController.class)
public class TransactionControllerTest {

    @MockBean
    private TransactionBaseService transactionService;

    @Autowired
    private MockMvc mockMvc;

    private TransactionDto transactionDtoCredit;
    private TransactionDto transactionDtoDebit;


    @Before
    public void before() {

        JacksonTester.initFields(this, new ObjectMapper());

        transactionDtoCredit = new TransactionDto.Builder().setTransactionType(1).setAmount(AMOUNT_1).setSuppliedId(SUPPLIED_ID_1)
                .setWalletId(1L).setDescription(DESCRIPTION).build();

        transactionDtoDebit = new TransactionDto.Builder().setTransactionType(2).setAmount(AMOUNT_2).setSuppliedId(SUPPLIED_ID_2)
                .setWalletId(1L).setDescription(DESCRIPTION_DEBIT).build();
    }

    @Test
    public void testGetTransactionWith_SuppliedId_Success() throws Exception {
        given(transactionService.transactionBySuppliedId(anyLong())).willReturn(transactionDtoCredit);
        mockMvc.perform(get("/transaction/{suppliedId}", SUPPLIED_ID_1)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suppliedId", is(transactionDtoCredit.getSuppliedId().intValue())))
                .andExpect(jsonPath("$.amount", is(transactionDtoCredit.getAmount().doubleValue())))
                .andExpect(jsonPath("$.walletId", is(transactionDtoCredit.getWalletId().intValue())))
                .andExpect(jsonPath("$.transactionType", is(transactionDtoCredit.getTransactionType())))
                .andExpect(jsonPath("$.description", is(transactionDtoCredit.getDescription())));
    }

    @Test
    public void testGetTransactionWith_PK_Success() throws Exception {
        given(transactionService.get(anyLong())).willReturn(transactionDtoDebit);
        mockMvc.perform(get("/transaction/pk/{id}", SUPPLIED_ID_2)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suppliedId", is(transactionDtoDebit.getSuppliedId().intValue())))
                .andExpect(jsonPath("$.amount", is(transactionDtoDebit.getAmount().doubleValue())))
                .andExpect(jsonPath("$.walletId", is(transactionDtoDebit.getWalletId().intValue())))
                .andExpect(jsonPath("$.transactionType", is(transactionDtoDebit.getTransactionType())))
                .andExpect(jsonPath("$.description", is(transactionDtoDebit.getDescription())));
    }

    @Test
    public void testCreateTransaction_Success() throws Exception {
        given(transactionService.create(any(TransactionDto.class))).willReturn(transactionDtoCredit);

        mockMvc.perform(post("/transaction")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(transactionDtoCredit))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suppliedId", is(transactionDtoCredit.getSuppliedId().intValue())))
                .andExpect(jsonPath("$.amount", is(transactionDtoCredit.getAmount().doubleValue())))
                .andExpect(jsonPath("$.walletId", is(transactionDtoCredit.getWalletId().intValue())))
                .andExpect(jsonPath("$.transactionType", is(transactionDtoCredit.getTransactionType())))
                .andExpect(jsonPath("$.description", is(transactionDtoCredit.getDescription())));
    }

    @Test
    public void testCreateTransaction_NoWalletId_Failed() throws Exception {
        given(transactionService.create(any(TransactionDto.class))).willReturn(transactionDtoCredit);
        TransactionDto dto = new TransactionDto.Builder()
                .setAmount(transactionDtoCredit.getAmount())
                .setSuppliedId(transactionDtoCredit.getSuppliedId())
                .setTransactionType(transactionDtoCredit.getTransactionType())
                .build();
        mockMvc.perform(post("/transaction")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(dto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Object Validation Failed")));
    }

    @Test
    public void testCreateTransaction_NoSuppliedId_Failed() throws Exception {
        given(transactionService.create(any(TransactionDto.class))).willReturn(transactionDtoCredit);
        TransactionDto dto = new TransactionDto.Builder()
                .setAmount(transactionDtoCredit.getAmount())
                .setWalletId(transactionDtoCredit.getWalletId())
                .setTransactionType(transactionDtoCredit.getTransactionType())
                .build();

        mockMvc.perform(post("/transaction")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(dto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}
