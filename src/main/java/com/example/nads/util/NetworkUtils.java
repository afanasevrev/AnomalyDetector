package com.example.nads.util;

public final class NetworkUtils {
    private NetworkUtils() {
    }

    public static boolean isInternalIp(String ip) {
        if (ip == null) return false;
        return ip.startsWith("10.");
    }

    public static boolean isTcp(String proto) {
        return "TCP".equalsIgnoreCase(proto);
    }

    public static boolean isUdp(String proto) {
        return "UDP".equalsIgnoreCase(proto);
    }

    public static boolean isIcmp(String proto) {
        return "ICMP".equalsIgnoreCase(proto);
    }

    public static double toSeconds(long millis) {
        return millis / 1000.0;
    }

}
