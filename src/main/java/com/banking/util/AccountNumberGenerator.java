package com.banking.util;

import java.security.SecureRandom;

public class AccountNumberGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String PREFIX = "1000";

    public static String generateAccountNumber() {
        long number = 10000000L + (long)(random.nextDouble() * 90000000L);
        return PREFIX + number;
    }

    public static String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + (1000 + random.nextInt(9000));
    }

    public static String generateIfscCode(String prefix, int branchCode) {
        return String.format("%s%03d", prefix, branchCode);
    }
}
