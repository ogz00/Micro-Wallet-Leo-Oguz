package com.oguz.demo.microwallet.service.player;

import com.oguz.demo.microwallet.dto.PlayerDto;
import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.entity.Player;
import com.oguz.demo.microwallet.entity.Wallet;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.helper.mapper.Mapper;
import com.oguz.demo.microwallet.repository.PlayerRepository;
import com.oguz.demo.microwallet.repository.WalletRepository;
import com.oguz.demo.microwallet.service.transaction.TransactionBaseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.oguz.demo.microwallet.exception.ErrorConstant.PLAYER_NOT_FOUND;

@Service
@Transactional
@Log4j2
public class DefaultPlayerService implements PlayerService {

    private PlayerRepository playerRepository;
    private WalletRepository walletRepository;
    private TransactionBaseService transactionService;

    @Autowired
    public DefaultPlayerService(PlayerRepository playerRepository, WalletRepository walletRepository,
                                TransactionBaseService transactionService) {
        this.playerRepository = playerRepository;
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    @Override
    public PlayerDto create(PlayerDto dto) throws MicroWalletException {
        Player player = Mapper.from(dto);
        player = playerRepository.save(player);
        return Mapper.from(player);
    }

    @Override
    @Transactional(rollbackFor = MicroWalletException.class)
    public PlayerDto get(@NotNull Long id) throws MicroWalletException {
        Player player = Optional.of(playerRepository.findById(id)).get().orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(PLAYER_NOT_FOUND, String.valueOf(id)));
        log.debug("Player found with id=" + id);
        return Mapper.from(player);
    }

    /**
     * @param playerName
     * @return
     */
    @Override
    @Transactional(rollbackFor = MicroWalletException.class)
    public List<WalletDto> transactionsByPlayerName(String playerName) {
        Player player = Optional.ofNullable(playerRepository.findByUsername(playerName)).orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(PLAYER_NOT_FOUND, playerName));
        List<Wallet> wallets = walletRepository.findByPlayer(player);
        log.debug("Transactions found for playerName=" + player.getUsername());
        return wallets.stream()
                .map(w -> Mapper.from(w, w.getTransactions().stream()
                        .map(Mapper::from).collect(Collectors.toList())))
                .collect(Collectors.toList());

    }

    /**
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = MicroWalletException.class)
    public List<WalletDto> transactionsByPlayerId(Long id) {
        Player player = findPlayer(id);
        List<Wallet> wallets = walletRepository.findByPlayer(player);
        log.debug("Transactions found for playerId=" + player.getId());
        return wallets.stream()
                .map(w -> Mapper.from(w, w.getTransactions().stream()
                        .map(Mapper::from).collect(Collectors.toList())))
                .collect(Collectors.toList());

    }

    private Player findPlayer(Long playerId) {
        return Optional.of(playerRepository.findById(playerId)).get().orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(PLAYER_NOT_FOUND, String.valueOf(playerId)));
    }

    /**
     * @param playerName
     * @return
     */
    @Override
    @Transactional(rollbackFor = MicroWalletException.class)
    public List<WalletDto> walletsByPlayerName(String playerName) {
        Player player = Optional.ofNullable(playerRepository.findByUsername(playerName)).orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(PLAYER_NOT_FOUND, playerName));
        List<Wallet> wallets = walletRepository.findByPlayer(player);
        log.debug("Wallets found for playerName=" + player.getUsername());
        return wallets.stream()
                .map(w -> Mapper.from(w, transactionService.calculateCurrentBalance(w.getId())))
                .collect(Collectors.toList());
    }

    /**
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = MicroWalletException.class)
    public List<WalletDto> walletsByPlayerId(Long id) {
        Player player = findPlayer(id);
        List<Wallet> wallets = walletRepository.findByPlayer(player);
        log.debug("Wallets found for playerId=" + player.getId());
        return wallets.stream()
                .map(w -> Mapper.from(w, transactionService.calculateCurrentBalance(w.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Player loadByUsername(String username) throws UsernameNotFoundException {
        Player player = playerRepository.findByUsername(username);
        if (player != null) return player;
        throw new UsernameNotFoundException("Player " + username + " not found!");
    }

    @Override
    public List<PlayerDto> getAll() {
        return playerRepository.findAll().stream().map(Mapper::from).collect(Collectors.toList());
    }
}
