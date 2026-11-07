package com.monthlystairbills.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Converts integer euro amounts to their Catalan word representation.
 * e.g. 60 -> "SEIXANTA EUROS"
 */
public class CatalanNumberWords {

    private static final String[] UNITS = {
        "", "UN", "DOS", "TRES", "QUATRE", "CINC", "SIS", "SET", "VUIT", "NOU",
        "DEU", "ONZE", "DOTZE", "TRETZE", "CATORZE", "QUINZE", "SETZE",
        "DISSET", "DIVUIT", "DINOU"
    };

    private static final String[] TENS = {
        "", "", "VINT", "TRENTA", "QUARANTA", "CINQUANTA",
        "SEIXANTA", "SETANTA", "VUITANTA", "NORANTA"
    };

    private static final String[] HUNDREDS = {
        "", "CENT", "DOS-CENTS", "TRES-CENTS", "QUATRE-CENTS", "CINC-CENTS",
        "SIS-CENTS", "SET-CENTS", "VUIT-CENTS", "NOU-CENTS"
    };

    /** Formats an amount with comma decimals, e.g. 157.29 -> "157,29", 60 -> "60". */
    public static String formatAmount(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString().replace('.', ',');
    }

    /** e.g. 157.29 -> "CENT CINQUANTA I SET EUROS AMB VINT-I-NOU CÈNTIMS" */
    public static String toWords(BigDecimal amount) {
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        int euros = scaled.intValue();
        int cents = scaled.subtract(new BigDecimal(euros)).movePointRight(2).intValue();
        if (cents == 0) return toWords(euros);
        String centsWords = toWordsInternal(cents).trim() + (cents == 1 ? " CÈNTIM" : " CÈNTIMS");
        if (euros == 0) return centsWords;
        return toWords(euros) + " AMB " + centsWords;
    }

    public static String toWords(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("L'import ha de ser positiu");
        return toWordsInternal(amount).trim() + " EUROS";
    }

    private static String toWordsInternal(int n) {
        if (n == 0) return "";
        if (n < 20) return UNITS[n];
        if (n < 30) {
            // 20 = VINT, 21..29 = VINT-I-UN, VINT-I-DOS, etc.
            return n == 20 ? "VINT" : "VINT-I-" + UNITS[n - 20];
        }
        if (n < 100) {
            String ten = TENS[n / 10];
            int rem = n % 10;
            return rem == 0 ? ten : ten + " I " + UNITS[rem];
        }
        if (n == 100) return "CENT";
        if (n < 200) return "CENT " + toWordsInternal(n - 100);
        if (n < 1000) {
            String hundred = HUNDREDS[n / 100];
            int rem = n % 100;
            return rem == 0 ? hundred : hundred + " " + toWordsInternal(rem);
        }
        if (n == 1000) return "MIL";
        if (n < 2000) return "MIL " + toWordsInternal(n - 1000);
        // 2000..9999
        String thousands = toWordsInternal(n / 1000) + " MIL";
        int rem = n % 1000;
        return rem == 0 ? thousands : thousands + " " + toWordsInternal(rem);
    }

    /** Returns the Catalan ordinal for a floor number (e.g. 2 -> "2n") */
    public static String floorOrdinal(int floor) {
        return switch (floor) {
            case 1 -> "1r";
            case 2 -> "2n";
            case 3 -> "3r";
            case 4 -> "4rt";
            default -> floor + "e";
        };
    }

    /** Returns the Catalan month name for a month number (1-12) */
    public static String monthName(int month) {
        return switch (month) {
            case 1  -> "gener";
            case 2  -> "febrer";
            case 3  -> "març";
            case 4  -> "abril";
            case 5  -> "maig";
            case 6  -> "juny";
            case 7  -> "juliol";
            case 8  -> "agost";
            case 9  -> "setembre";
            case 10 -> "octubre";
            case 11 -> "novembre";
            case 12 -> "desembre";
            default -> throw new IllegalArgumentException("Mes invalid: " + month);
        };
    }
}
