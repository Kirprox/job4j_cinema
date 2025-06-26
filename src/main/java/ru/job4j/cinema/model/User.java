package ru.job4j.cinema.model;

import java.util.Map;

public class User {
    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "id", "id",
            "full_name", "fullName",
            "email", "email",
            "password", "password"
    );
    private int id;
    private String fullName;
    private String email;
    private String password;
}
