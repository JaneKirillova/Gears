package com.example.gears;

public class User {
    private final String token;
    private final String username;
    private final String password;
    private final Long id;
    private int points = 0;
    private int totalNumberOfGames = 0;
    private int gamesWon = 0;
    private int gamesLost = 0;

    public Integer getTotalNumberOfGames() {
        return totalNumberOfGames;
    }

    public Integer getGamesWon() {
        return gamesWon;
    }

    public Integer getGamesLost() {
        return gamesLost;
    }

    public User(String token, String username, String password, Long id) {
        this.token = token;
        this.username = username;
        this.password = password;
        this.id = id;
    }

    public User(String token, String username, String password, Long id, int points, int totalNumberOfGames, int gamesWon, int gamesLost) {
        this(token, username, password, id);
        this.points = points;
        this.gamesLost = gamesLost;
        this.gamesWon = gamesWon;
        this.totalNumberOfGames = totalNumberOfGames;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Integer getPoints() {
        return points;
    }

    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }

}
