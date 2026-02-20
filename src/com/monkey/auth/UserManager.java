package com.monkey.auth;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserManager {

    private static final String FILE_PATH = "users.json";
    private static UserManager instance;
    private final List<User> users;

    // --- CHANGED SUPER USER TO "adi" ---
    public static final String SUPER_USER = "adi";

    private UserManager() {
        users = new ArrayList<>();
        loadUsers();

        // 1. Ensure "adi" exists
        if (getUser(SUPER_USER) == null) {
            // Default password for adi is "admin"
            User root = new User(SUPER_USER, "admin", User.Role.DEVELOPER, new JSONObject());
            users.add(root);
            saveUsers();
        }
        // 2. FORCE UPDATE: Ensure "adi" is always DEVELOPER
        else {
            User root = getUser(SUPER_USER);
            if (!root.isDeveloper()) {
                root.setRole(User.Role.DEVELOPER);
                saveUsers();
            }
        }
    }

    public static UserManager getInstance() {
        if (instance == null) instance = new UserManager();
        return instance;
    }

    public User login(String username, String password) {
        User u = getUser(username);
        if (u != null && u.checkPassword(password)) {
            return u;
        }
        return null;
    }

    public boolean register(String username, String password) {
        if (getUser(username) != null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        User newUser = new User(username, password, User.Role.PLAYER, new JSONObject());
        users.add(newUser);
        saveUsers();
        return true;
    }

    public void deleteUser(User u) {
        if (u.getUsername().equalsIgnoreCase(SUPER_USER)) return;
        users.remove(u);
        saveUsers();
    }

    public User getUser(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    public List<User> getAllUsers() {
        return users;
    }

    public void saveUsers() {
        try {
            JSONObject root = new JSONObject();
            JSONArray arr = new JSONArray();
            for (User u : users) {
                arr.put(u.toJSON());
            }
            root.put("users", arr);

            try (FileWriter fw = new FileWriter(FILE_PATH)) {
                fw.write(root.toString(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        File f = new File(FILE_PATH);
        if (!f.exists()) return;

        try {
            String content = new String(Files.readAllBytes(Paths.get(f.getPath())));
            JSONObject root = new JSONObject(content);
            JSONArray arr = root.getJSONArray("users");

            users.clear();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                User.Role r = User.Role.PLAYER;
                if (obj.has("role")) {
                    r = User.Role.valueOf(obj.getString("role"));
                } else if (obj.optBoolean("isAdmin", false)) {
                    r = User.Role.ADMIN;
                }

                User u = new User(
                        obj.getString("username"),
                        obj.getString("password"),
                        r,
                        obj.optJSONObject("progress")
                );
                users.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
