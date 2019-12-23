package com.oguz.demo.microwallet.controller;

import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.service.wallet.WalletService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/wallet", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class WalletController {

    private WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<WalletDto> getWallet(@PathVariable("id") Long walletId) throws MicroWalletException {
        return new ResponseEntity<>(walletService.get(walletId), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/balance")
    public ResponseEntity<BigDecimal> getWalletBalance(@PathVariable("id") Long walletId) throws MicroWalletException {
        return new ResponseEntity<>(walletService.get(walletId).getBalance(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/transactions")
    public ResponseEntity<WalletDto> getWalletTransactions(@PathVariable("id") Long walletId) throws MicroWalletException {
        return new ResponseEntity<>(walletService.transactionsByWalletId(walletId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<WalletDto> createWallet(@Valid @RequestBody WalletDto walletDto) {
        log.debug("Request WalletController.createWallet");
        return new ResponseEntity<>(walletService.create(walletDto), HttpStatus.OK);
    }

}
