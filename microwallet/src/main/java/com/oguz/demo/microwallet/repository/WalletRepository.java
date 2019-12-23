package com.oguz.demo.microwallet.repository;

import com.oguz.demo.microwallet.entity.Currency;
import com.oguz.demo.microwallet.entity.Player;
import com.oguz.demo.microwallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByPlayer(Player player);

    List<Wallet> findByPlayerAndCurrency(Player player, Currency currency);
}
