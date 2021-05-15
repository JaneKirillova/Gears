package com.example.gears;

public class User {
    private String token;
    private String username, password;
    Long id;
    int points = 0;

    public User(String token, String username, String password, Long id) {
        this.token = token;
        this.username = username;
        this.password = password;
        this.id = id;
    }

    public User(String token, String username, String password, Long id, int points) {
        this(token, username, password, id);
        this.points = points;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public int getPoints() {
        return points;
    }

    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }

}
