package com.example.tinyurl.utils;

import java.net.URI;

public class UrlNormalizer {

    private UrlNormalizer() {}

    public static String normalize(String url) {
        try {
            URI uri = new URI(url.trim());

            String scheme = uri.getScheme().toLowerCase();
            String host = uri.getHost().toLowerCase();
            int port = uri.getPort();
            String path = uri.getPath();

            // Force https
            scheme = "https";

            // Remove default ports
            if (port == 80 || port == 443) {
                port = -1;
            }

            // Remove trailing slash
            if (path != null && path.endsWith("/") && path.length() > 1) {
                path = path.substring(0, path.length() - 1);
            }

            URI normalized = new URI(
                    scheme,
                    null,
                    host,
                    port,
                    path,
                    null,
                    null
            );

            return normalized.toString();

        } catch (Exception e) {
            return url;
        }
    }
}
