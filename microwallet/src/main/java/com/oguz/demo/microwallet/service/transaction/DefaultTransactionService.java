package com.oguz.demo.microwallet.service.transaction;

import com.oguz.demo.microwallet.dto.TransactionDto;
import com.oguz.demo.microwallet.entity.Transaction;
import com.oguz.demo.microwallet.entity.TransactionType;
import com.oguz.demo.microwallet.entity.Wallet;
import com.oguz.demo.microwallet.exception.MicroWalletException;
import com.oguz.demo.microwallet.helper.mapper.Mapper;
import com.oguz.demo.microwallet.repository.TransactionRepository;
import com.oguz.demo.microwallet.repository.WalletRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.oguz.demo.microwallet.exception.ErrorConstant.*;

@Service
@Transactional
@Log4j2
public class DefaultTransactionService implements TransactionBaseService {

    private TransactionRepository transactionRepository;
    private WalletRepository walletRepository;

    @Autowired
    public DefaultTransactionService(TransactionRepository transactionRepository,
                                     WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    /**
     * Method for create new transaction.
     * This method firstly check if wallet exist. If wallet exist, calculate its current balance with top transaction logic.
     * Then analyze transaction type and check is it a valid transaction request.
     * If requested transaction amount is valid creates the transaction and returns success dto.
     * <p>
     * Since wallet balance calculated with derivative ways, this method doesn't need to update any existing object in database.
     * Because of that, in case of high transaction traffic, this feature provides high performance.
     * <p>
     * <p>
     * Since transaction is connected to wallet directly, there is no need to check currency type for this request.
     * Because of the transaction dto have wallet id field, assume that API users know which currency belongs to transactionDto wallet.
     *
     * @param dto requested transaction dto object.
     * @return successfully created transaction dto.
     * @throws MicroWalletException if wallet is not exist, or it's invalid transaction request.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = MicroWalletException.class)
    public TransactionDto create(TransactionDto dto) throws MicroWalletException {

        Wallet wallet = Optional.of(walletRepository.findById(dto.getWalletId())).get().orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(WALLET_NOT_FOUND, String.valueOf(dto.getWalletId())));

        TransactionType type = Mapper.getTransactionType(dto.getTransactionType());
        BigDecimal currentBalance = calculateCurrentBalance(wallet.getId());
        if (type.equals(TransactionType.DEBIT)) {
            if (!isValidTransactions(dto.getAmount(), currentBalance)) {
                throw MicroWalletException.Builder
                        .newInstance().buildWithStringFormatter(INSUFFICIENT_WALLET_BALANCE, String.valueOf(dto.getWalletId()), String.valueOf(dto.getAmount()));
            }
        }
        Transaction transaction = Mapper.from(dto);
        transaction.setWallet(wallet);
        transaction.setOpeningBalance(currentBalance);
        transaction = transactionRepository.saveAndFlush(transaction);
        log.debug("Transaction created with id=" + transaction.getId());

        return Mapper.from(transaction);

    }

    /**
     * Method for check is requested 'Debit' transaction is valid.
     * This method must only call with debit transactions.
     *
     * @param amount         Requested debit amount
     * @param currentBalance current balance for requested wallet.
     * @return if transaction is possible returns true.
     */
    private boolean isValidTransactions(BigDecimal amount, BigDecimal currentBalance) {
        return currentBalance.abs().compareTo(amount.abs()) >= 0;
    }

    /**
     * Method for gathering transaction with auto-generate Id.
     * Returns transactionDto if transaction exist, else throw error.
     *
     * @param id requested transaction id called from controller.
     * @return TransactionDto
     * @throws MicroWalletException in case of transaction not exist.
     */
    @Override
    @Transactional(rollbackFor = MicroWalletException.class)
    public TransactionDto get(@NotNull Long id) throws MicroWalletException {
        Transaction transaction = findTransactionById(id);
        log.debug("Transaction find with id=" + transaction.getId());
        return Mapper.from(transaction);
    }

    private Transaction findTransactionById(Long id) {
        return Optional.of(transactionRepository.findById(id)).get().orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(TRANSACTION_NOT_FOUND, String.valueOf(id)));
    }

    /**
     * Method for gathering transaction with supplied Id.
     * Returns transactionDto if transaction exist, else throw error.
     *
     * @param suppliedId requested transaction suppliedId called from controller.
     * @return TransactionDto
     * @throws MicroWalletException in case of transaction not exist.
     */
    @Override
    @Transactional(rollbackFor = MicroWalletException.class)
    public TransactionDto transactionBySuppliedId(Long suppliedId) throws MicroWalletException {
        Transaction transaction = Optional.of(transactionRepository.findBySuppliedId(suppliedId)).get().orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(TRANSACTION_NOT_FOUND, String.valueOf(suppliedId)));
        log.debug("Transaction find with suppliedId=" + transaction.getId());
        return Mapper.from(transaction);
    }

    /**
     * Method for gathering wallet transaction history.
     * Returns transactionDto list if wallet exist, else throw error.
     *
     * @param walletId requested wallet id called from controller.
     * @return List</ TransactionDto>
     * @throws MicroWalletException in case of wallet not exist.
     */
    @Override
    @Transactional(rollbackFor = MicroWalletException.class)
    public List<TransactionDto> transactionsByWalletId(@NotNull Long walletId) throws MicroWalletException {
        Wallet wallet = Optional.of(walletRepository.findById(walletId)).get().orElseThrow(
                () -> MicroWalletException.Builder.newInstance().buildWithStringFormatter(WALLET_NOT_FOUND, String.valueOf(walletId)));
        List<Transaction> transactions = transactionRepository.findAllByWallet(wallet);
        return transactions.stream()
                .map(Mapper::from)
                .collect(Collectors.toList());

    }

    /**
     * Method for call top transaction record from database.
     * Since it request only 1 line Select query, provide service a fast calculating process.
     *
     * @param walletId requested wallet id
     * @return Optional object of transaction.
     */
    @Transactional(rollbackFor = MicroWalletException.class)
    public Optional<Transaction> findTopTransaction(Long walletId) {
        return Optional.ofNullable(transactionRepository.findTopTransaction(walletId));
    }

    /**
     * Method for calculate wallet balance, helps with findTopTransaction func.
     * If there isn't any transaction for requested wallet, assume that it's freshly new wallet returns zero.
     *
     * <p>This logic can be develops like in every opening wallet should have opening transaction records but
     * currently there is a ambiguous state because of assessment define. Explained below:
     * <span>
     * If every transaction must exist with only and only supplied id, there is no chance to generate an unique value for auto created transaction's supplied Id.
     * As a result service can wait opening transaction supplied Id in createWallet request but since it's a demo project, I think its enough to just mentioned it.
     * </span>
     * </p>
     *
     * @param walletId requested wallet Id
     * @return wallet balance in BigDecimal format, according to logic explained before.
     */
    @Override
    @Transactional(rollbackFor = MicroWalletException.class)
    public BigDecimal calculateCurrentBalance(Long walletId) {
        Optional<Transaction> topTransaction = findTopTransaction(walletId);
        if (topTransaction.isPresent())
            return topTransaction.get().getOpeningBalance().add(topTransaction.get().getAmount());
        return new BigDecimal(0);
    }

}
