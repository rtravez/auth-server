package com.rtravez.auth.server.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {
    public static final String MSC_WEB_CLIENT_ID = "MSC-WEB";
    public static final String MSC_WS_CLIENT_ID = "MSC-WS";
    public static final String MSA_WS_CLIENT_ID = "MSA-WS";
    public static final String MSC_CLIENT_SECRET = "$2a$10$itMT/hPoLNo/FqNuzh69LeNMMUsca/j2WIdOn7P0ZYhjs6rKK/way";
    public static final String MSA_CLIENT_SECRET = "$2a$10$itMT/hPoLNo/FqNuzh69LeNMMUsca/j2WIdOn7P0ZYhjs6rKK/way";
}
