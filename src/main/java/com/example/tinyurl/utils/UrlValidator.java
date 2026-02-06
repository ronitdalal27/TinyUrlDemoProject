package com.example.tinyurl.utils;

import java.net.URI;

public class UrlValidator {

    private UrlValidator() {}

    public static boolean isValid(String url) {
        try {
            URI uri = new URI(url);

            // Must have scheme and host
            if (uri.getScheme() == null || uri.getHost() == null) {
                return false;
            }

            // Allow only http & https
            String scheme = uri.getScheme().toLowerCase();
            return scheme.equals("http") || scheme.equals("https");

        } catch (Exception e) {
            return false;
        }
    }
}
