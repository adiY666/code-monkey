package com.monkey.auth;

import org.json.JSONObject;

public class User {

    public enum Role {
        PLAYER, ADMIN, DEVELOPER
    }

    private String username;
    private String password;
    private Role role;
    private JSONObject progress;

    public User(String username, String password, Role role, JSONObject progress) {
        this.username = username;
        this.password = password;
        this.role = role != null ? role : Role.PLAYER;
        this.progress = progress != null ? progress : new JSONObject();
    }

    public String getUsername() { return username; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    // Logic Helpers
    public boolean isDeveloper() {
        return role == Role.DEVELOPER;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isAdminOrDev() {
        return role == Role.ADMIN || role == Role.DEVELOPER;
    }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    public int getStars(String levelName) {
        return progress.optInt(levelName, 0);
    }

    public void setStars(String levelName, int stars) {
        progress.put(levelName, stars); // Allows overwriting (Admin giving progress)
    }

    public void resetProgress() {
        this.progress = new JSONObject();
    }

    public JSONObject getProgressData() { return progress; }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        json.put("role", role.name()); // Save Role as String
        json.put("progress", progress);
        return json;
    }
}
