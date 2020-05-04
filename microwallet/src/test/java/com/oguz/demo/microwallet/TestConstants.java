package com.oguz.demo.microwallet;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TestConstants {

    public static final String PLAYER_NAME = "Test Player";
    public static final String PLAYER_USERNAME = "Username";
    public static final String PLAYER_PASSWORD = "TestPassword";
    public static final String PLAYER_COUNTRY = "Country";
    public static final String CURRENCY_NAME = "Test Currency";
    public static final String CURRENCY_CODE = "TCR";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String DESCRIPTION_DEBIT = "DESCRIPTION_DEBIT";
    public static final Long SUPPLIED_ID_1 = 1L;
    public static final Long SUPPLIED_ID_2 = 2L;
    public static final BigDecimal OPENING_AMOUNT = new BigDecimal(0);
    public static final BigDecimal AMOUNT_1 = new BigDecimal(80.15).setScale(2, RoundingMode.HALF_UP);
    public static final BigDecimal AMOUNT_2 = new BigDecimal(20.25).setScale(2, RoundingMode.HALF_UP);

    public static final Integer _ID = 1;
}
