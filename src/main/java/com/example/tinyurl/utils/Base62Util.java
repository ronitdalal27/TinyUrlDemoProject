package com.example.tinyurl.utils;

public class Base62Util {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encode(long number) {
        StringBuilder sb = new StringBuilder();

        while (number > 0) {
            sb.insert(0, BASE62.charAt((int) (number % 62)));
            number /= 62;
        }

        // Ensure fixed length of 7
        while (sb.length() < 7) {
            sb.insert(0, '0');
        }

        return sb.toString();
    }
}
