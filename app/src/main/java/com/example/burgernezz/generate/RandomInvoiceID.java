package com.example.burgernezz.generate;

import java.util.Random;

public class RandomInvoiceID {
    private final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private final String NUMBERS = "0123456890";
    private final char[] ALPHANUMERIC = (LETTERS+LETTERS.toUpperCase()+NUMBERS).toCharArray();

    public String generateInvoiceID(int length){
        StringBuilder results = new StringBuilder();
        for (int i = 0; i < length ; i++){
            results.append(ALPHANUMERIC[new Random().nextInt(ALPHANUMERIC.length)]);
        }
        return results.toString();
    }
}
