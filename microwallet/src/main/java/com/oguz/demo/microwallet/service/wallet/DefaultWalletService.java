package com.oguz.demo.microwallet.service.wallet;

import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.entity.Currency;
import com.oguz.demo.microwallet.entity.Player;
import com.oguz.demo.microwallet.entity.Wallet;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.helper.mapper.Mapper;
import com.oguz.demo.microwallet.repository.CurrencyRepository;
import com.oguz.demo.microwallet.repository.PlayerRepository;
import com.oguz.demo.microwallet.repository.WalletRepository;
import com.oguz.demo.microwallet.service.transaction.TransactionBaseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.oguz.demo.microwallet.exception.ErrorConstant.*;

@Service
@Transactional
@Log4j2
public class DefaultWalletService implements WalletService {

    private WalletRepository walletRepository;
    private PlayerRepository playerRepository;
    private CurrencyRepository currencyRepository;
    private TransactionBaseService transactionService;

    @Autowired
    public DefaultWalletService(WalletRepository walletRepository,
                                PlayerRepository playerRepository,
                                CurrencyRepository currencyRepository,
                                TransactionBaseService transactionService) {

        this.walletRepository = walletRepository;
        this.playerRepository = playerRepository;
        this.currencyRepository = currencyRepository;
        this.transactionService = transactionService;
    }

    /**
     * @param dto Requested WalletDto object
     * @return successfully created WalletDto with Id
     * @throws MicroWalletException
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = MicroWalletException.class)
    @Override
    public WalletDto create(WalletDto dto) throws MicroWalletException {
        try {
            Player player = findPlayer(dto.getPlayerId());
            Currency currency = findCurrency(dto.getCurrencyId());

            Wallet dbWallet = walletRepository.saveAndFlush(new Wallet(player, currency));

            return Mapper.from(dbWallet, new BigDecimal(0));

        } catch (RuntimeException ex) {
            throw MicroWalletException.Builder.newInstance().setRootException(ex)
                    .setMessage(INTERNAL_SERVER_ERROR)
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * @param id Requested Wallet Id
     * @return Wallet Dto, exclude transaction History
     * @throws MicroWalletException
     */
    @Transactional(rollbackFor = MicroWalletException.class)
    @Override
    public WalletDto get(Long id) throws MicroWalletException {
        Wallet wallet = findWallet(id);
        return Mapper.from(wallet, transactionService.calculateCurrentBalance(id));
    }

    /**
     * @param playerId   Requested player Id
     * @param currencyId Requested currency Id
     * @return WalletDto list, exclude transaction histories.
     */
    @Transactional(rollbackFor = MicroWalletException.class)
    @Override
    public List<WalletDto> walletsByPlayerIdAndCurrencyId(Long playerId, Integer currencyId) {
        Player player = findPlayer(playerId);
        Currency currency = findCurrency(currencyId);

        List<Wallet> wallets = walletRepository.findByPlayerAndCurrency(player, currency);

        return wallets.stream()
                .map(w -> Mapper.from(w, transactionService.calculateCurrentBalance(w.getId())))
                .collect(Collectors.toList());
    }


    /**
     * @param id Requested Wallet Id
     * @return WalletDto With Balance and Transaction History
     */
    @Override
    @Transactional(rollbackFor = MicroWalletException.class)
    public WalletDto transactionsByWalletId(Long id) {
        Wallet wallet = findWallet(id);
        log.debug("Transactions found for walletId=" + wallet.getId());
        WalletDto walletDto = Mapper.from(wallet, wallet.getTransactions().stream().map(Mapper::from).collect(Collectors.toList()));
        walletDto.setBalance(transactionService.calculateCurrentBalance(wallet.getId()));
        return walletDto;

    }

    private Wallet findWallet(Long id) {
        return Optional.of(walletRepository.findById(id)).get().orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(WALLET_NOT_FOUND, String.valueOf(id)));
    }

    private Player findPlayer(Long playerId) {
        return Optional.of(playerRepository.findById(playerId)).get().orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(PLAYER_NOT_FOUND, String.valueOf(playerId)));
    }

    private Currency findCurrency(Integer currencyId) {
        return Optional.of(currencyRepository.findById(currencyId)).get().orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(CURRENCY_NOT_FOUND, String.valueOf(currencyId)));
    }


}
