package com.example.smartagri.common;

public final class CurrentUser {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    private CurrentUser() {
    }

    public static void set(Long userId, String username) {
        USER_ID.set(userId);
        USERNAME.set(username);
    }

    public static Long id() {
        return USER_ID.get();
    }

    public static String username() {
        String username = USERNAME.get();
        return username == null ? "system" : username;
    }

    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
    }
}
