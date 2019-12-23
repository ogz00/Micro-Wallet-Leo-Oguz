package com.oguz.demo.microwallet.service.player;

import com.oguz.demo.microwallet.dto.PlayerDto;
import com.oguz.demo.microwallet.dto.WalletDto;
import com.oguz.demo.microwallet.service.base.BaseService;

import java.util.List;

public interface PlayerService extends BaseService<PlayerDto> {
    List<PlayerDto> getAll();
    List<WalletDto> transactionsByPlayerName(String playerName);
    List<WalletDto> transactionsByPlayerId(Long id);
    List<WalletDto> walletsByPlayerName(String name);
    List<WalletDto> walletsByPlayerId(Long id);
}
