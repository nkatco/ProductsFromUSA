package com.example.productsfromusa.utils;

public class TextUtils {
    public static String escapeMarkdownV2(String text) {
        return text.replaceAll(".", "\\.");
    }
}
