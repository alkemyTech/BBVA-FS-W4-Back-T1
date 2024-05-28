package com.magicdogs.alkywall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Constants {

    private static double TRANSACTION_LIMIT_ARS;
    private static double TRANSACTION_LIMIT_USD;

    @Autowired
    public Constants(Environment env) {
        TRANSACTION_LIMIT_ARS = Double.parseDouble(Objects.requireNonNull(env.getProperty("transaction.limit.ars")));
        TRANSACTION_LIMIT_USD = Double.parseDouble(Objects.requireNonNull(env.getProperty("transaction.limit.usd")));
    }

    public static double getTransactionLimitArs() {
        return TRANSACTION_LIMIT_ARS;
    }

    public static double getTransactionLimitUsd() {
        return TRANSACTION_LIMIT_USD;
    }
}

