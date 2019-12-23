package com.oguz.demo.microwallet.controller;

import com.oguz.demo.microwallet.dto.CurrencyDto;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.service.currency.CurrencyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/currency", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class CurrencyController {

    private CurrencyService service;

    @Autowired
    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    /**
     * Controller method for fetch all currencies.
     *
     * @return List of currencyDto
     * @throws MicroWalletException CustomExceptionHandler Object
     */
    @GetMapping
    public ResponseEntity<List<CurrencyDto>> getAllCurrencies() throws MicroWalletException {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    /**
     * Controller method for create Currency, CurrencyDto object expected as a Json Object in request body.
     *
     * @param dto CurrencyDto json object
     * @return created CurrencyDto
     * @throws MicroWalletException CustomExceptionHandler Object
     */
    @PostMapping
    public ResponseEntity<CurrencyDto> createCurrency(@Valid @RequestBody CurrencyDto dto) throws MicroWalletException {
        log.debug("Called PlayerController.createPlayer");
        return new ResponseEntity<>(service.create(dto), HttpStatus.OK);
    }
}
