package com.monkey.auth;

import org.json.JSONObject;

public class User {

    public enum Role {
        PLAYER, ADMIN, DEVELOPER
    }

    private final String username; // Immutable
    private final String password; // Immutable
    private Role role;
    private JSONObject progress;

    // Added 'final' to parameters
    public User(final String username, final String password, final Role role, final JSONObject progress) {
        this.username = username;
        this.password = password;
        this.role = role != null ? role : Role.PLAYER;
        this.progress = progress != null ? progress : new JSONObject();
    }

    public String getUsername() { return username; }

    public Role getRole() { return role; }

    public void setRole(final Role role) {
        this.role = role;
    }

    // --- Logic Helpers ---

    public boolean isDeveloper() {
        return role == Role.DEVELOPER;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isAdminOrDev() {
        return role == Role.ADMIN || role == Role.DEVELOPER;
    }

    public boolean checkPassword(final String input) {
        return password.equals(input);
    }

    public int getStars(final String levelName) {
        return progress.optInt(levelName, 0);
    }

    public void setStars(final String levelName, final int stars) {
        progress.put(levelName, stars);
    }

    public void resetProgress() {
        this.progress = new JSONObject();
    }

    public JSONObject getProgressData() {
        return progress;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        json.put("role", role.name());
        // Now using the getter as requested
        json.put("progress", getProgressData());
        return json;
    }
}
