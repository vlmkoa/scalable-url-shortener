package com.ryanvo.url_shortener.service;

public class Base62Converter {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] ALPHABET = BASE62.toCharArray();
    private static final int BASE = ALPHABET.length;

    public static String encode(long id) {
        if (id == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(ALPHABET[(int) (id % BASE)]);
            id /= BASE;
        }
        return sb.reverse().toString();
    }

    public static long decode(String str) {
        long id = 0;
        for (char c : str.toCharArray()) {
            id = id * BASE + BASE62.indexOf(c);
        }
        return id;
    }
}
