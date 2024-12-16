package com.clinic;

class MessageFormatter {
    public static String success(String message) { return "✅ " + message; }
    public static String error(String message) { return "🔴 " + message; }
    public static String prompt(String message) { return "🟢 " + message; }
    public static String info(String message) { return "ℹ️ " + message; }
}