package com.oguz.demo.microwallet.controller;

import com.oguz.demo.microwallet.dto.TransactionDto;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.service.transaction.TransactionBaseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class TransactionController {

    @Qualifier("defaultTransactionService")
    private TransactionBaseService transactionService;

    @Autowired
    public TransactionController(TransactionBaseService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     *
     * @param suppliedId User-Supplied Unique Id.
     * @return TransactionDto
     * @throws MicroWalletException
     */
    @GetMapping(value = "/{suppliedId}")
    public ResponseEntity<TransactionDto> getTransactionWithSuppliedId(@PathVariable("suppliedId") Long suppliedId) throws MicroWalletException {
        return new ResponseEntity<>(transactionService.transactionBySuppliedId(suppliedId), HttpStatus.OK);
    }

    /**
     *
     * @param id Auto-Generated Primary Key
     * @return TransactionDto
     * @throws MicroWalletException
     */
    @GetMapping(value = "/pk/{id}")
    public ResponseEntity<TransactionDto> getTransactionWithPK(@PathVariable("id") Long id) throws MicroWalletException {
        return new ResponseEntity<>(transactionService.get(id), HttpStatus.OK);
    }

    /**
     *
     * @param dto TransactionDto reference as a Body parameter in JSON format.
     * @return TransactionDto
     * @throws MicroWalletException
     */
    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody TransactionDto dto) throws MicroWalletException {
        log.debug("Called TransactionController.createTransaction");
        return new ResponseEntity<>(transactionService.create(dto), HttpStatus.OK);
    }
}
