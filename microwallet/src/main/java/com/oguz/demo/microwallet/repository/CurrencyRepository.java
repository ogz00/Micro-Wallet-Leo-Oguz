package com.oguz.demo.microwallet.repository;

import com.oguz.demo.microwallet.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
}
