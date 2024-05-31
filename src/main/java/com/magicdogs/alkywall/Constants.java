package com.magicdogs.alkywall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Constants {

    private static double TRANSACTION_LIMIT_ARS;
    private static double TRANSACTION_LIMIT_USD;
    private static String JWT_COOKIE_NAME;
    private static int JWT_EXPIRATION_MINUTES;
    private static int FIXED_TERM_MINIMUM_DAYS;
    private static double FIXED_TERM_INTEREST_PERCENT;
    private static double LOAN_INTEREST_PERCENT;

    @Autowired
    public Constants(Environment env) {
        TRANSACTION_LIMIT_ARS = Double.parseDouble(Objects.requireNonNull(env.getProperty("transaction.limit.ars")));
        TRANSACTION_LIMIT_USD = Double.parseDouble(Objects.requireNonNull(env.getProperty("transaction.limit.usd")));
        JWT_COOKIE_NAME = Objects.requireNonNull(env.getProperty("security.jwt.cookie.name"));
        JWT_EXPIRATION_MINUTES = Integer.parseInt(Objects.requireNonNull(env.getProperty("security.jwt.expiration.minutes")));
        FIXED_TERM_MINIMUM_DAYS = Integer.parseInt(Objects.requireNonNull(env.getProperty("fixedTermDeposit.minimum.days")));
        FIXED_TERM_INTEREST_PERCENT = Double.parseDouble(Objects.requireNonNull(env.getProperty("fixedTermDeposit.interest.percent")));
        LOAN_INTEREST_PERCENT = Double.parseDouble(Objects.requireNonNull(env.getProperty("loan.interest.percent")));

    }

    public static double getTransactionLimitArs() {
        return TRANSACTION_LIMIT_ARS;
    }

    public static double getTransactionLimitUsd() {
        return TRANSACTION_LIMIT_USD;
    }

    public static String getJwtCookieName() {
        return JWT_COOKIE_NAME;
    }

    public static int getJwtExpirationMinutes() {
        return JWT_EXPIRATION_MINUTES;
    }

    public static int getFixedTermMinimunDays() {
        return FIXED_TERM_MINIMUM_DAYS;
    }

    public static double getFixedTermInterestPercent() {
        return FIXED_TERM_INTEREST_PERCENT;
    }

    public static double getLoanInterestPercent() {
        return LOAN_INTEREST_PERCENT;
    }

}

