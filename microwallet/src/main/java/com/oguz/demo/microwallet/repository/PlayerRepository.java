package com.oguz.demo.microwallet.repository;

import com.oguz.demo.microwallet.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByName(String name);
}
