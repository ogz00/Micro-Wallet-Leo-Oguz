package com.oguz.demo.microwallet.controller;

import com.oguz.demo.microwallet.dto.PlayerDto;
import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.service.player.PlayerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/player", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class PlayerController {

    private PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Controller method for fetch all player.
     *
     * @return List of playerDto
     * @throws MicroWalletException CustomExceptionHandler Object
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PLAYER') OR hasAuthority('ADMIN_USER')")
    public ResponseEntity<List<PlayerDto>> getAllPlayers() throws MicroWalletException {
        return new ResponseEntity<>(playerService.getAll(), HttpStatus.OK);
    }

    /**
     * Controller method for create Player, playerDto object expected as a Json Object in request body.
     *
     * @param dto PlayerDto json object
     * @return created playerDto
     * @throws MicroWalletException CustomExceptionHandler Object
     */
    @PostMapping
//    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public ResponseEntity<PlayerDto> createPlayer(@Valid @RequestBody PlayerDto dto) throws MicroWalletException {
        log.debug("Called PlayerController.createPlayer");
        PlayerDto savedDto = playerService.create(dto);
        return new ResponseEntity<>(savedDto, HttpStatus.OK);
    }

    /**
     * Controller method for fetching Players wallet information with name
     *
     * @param name requested player name
     * @return walletDto list, transactions history were excluded.
     * @throws MicroWalletException CustomExceptionHandler Object
     */
    @GetMapping(value = "/{name}/wallets")
    @PreAuthorize("hasAnyAuthority('PLAYER') OR hasAuthority('ADMIN_USER')")
    public ResponseEntity<List<WalletDto>> getWalletsByPlayerName(@PathVariable("name") String name) throws MicroWalletException {
        return new ResponseEntity<>(playerService.walletsByPlayerName(name), HttpStatus.OK);
    }

    /**
     * Controller method for fetching Players wallet information with id
     *
     * @param id requested player id
     * @return walletDto list, transactions history were excluded.
     * @throws MicroWalletException CustomExceptionHandler Object
     */
    @PreAuthorize("hasAnyAuthority('PLAYER') OR hasAuthority('ADMIN_USER')")
    @GetMapping(value = "/{id}/wallets")
    public ResponseEntity<List<WalletDto>> getWalletsByPlayerId(@PathVariable("id") Long id) throws MicroWalletException {
        return new ResponseEntity<>(playerService.walletsByPlayerId(id), HttpStatus.OK);
    }

    /**
     * Since players transactions is directly connected to players wallets,
     * players can check transaction history according to their wallets.
     * Because of this connection this request returns list of wallets included with transactions.
     *
     * @param name requested player name
     * @return walletDto list, transactions history were included.
     * @throws MicroWalletException CustomExceptionHandler Object
     */
    @PreAuthorize("hasAnyAuthority('PLAYER') OR hasAuthority('ADMIN_USER')")
    @GetMapping(value = "/{name}/transactionsWithName")
    public ResponseEntity<List<WalletDto>> getTransactionsByPlayerName(@PathVariable("name") String name) throws MicroWalletException {
        return new ResponseEntity<>(playerService.transactionsByPlayerName(name), HttpStatus.OK);
    }

    /**
     * Since players transactions is directly connected to players wallets,
     * players can check transaction history according to their wallets.
     * Because of this connection this request returns list of wallets included with transactions.
     *
     * @param id requested player Id
     * @return walletDto list, transactions history were included.
     * @throws MicroWalletException CustomExceptionHandler Object
     */
    @PreAuthorize("hasAnyAuthority('PLAYER') OR hasAuthority('ADMIN_USER')")
    @GetMapping(value = "/{id}/transactions")
    public ResponseEntity<List<WalletDto>> getTransactionsByPlayerId(@PathVariable("id") Long id) throws MicroWalletException {
        return new ResponseEntity<>(playerService.transactionsByPlayerId(id), HttpStatus.OK);
    }

}
