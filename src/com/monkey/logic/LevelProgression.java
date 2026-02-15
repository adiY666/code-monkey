package com.monkey.logic;

import com.monkey.auth.User;
import com.monkey.auth.UserManager;
import java.io.File;
import java.util.Arrays;

public class LevelProgression {

    public static File getNextLevel(File currentFile) {
        if(currentFile == null) return null;
        File folder = currentFile.getParentFile();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if(files != null) {
            Arrays.sort(files);
            for(int i = 0; i < files.length; i++) {
                if(files[i].equals(currentFile)) {
                    if(i + 1 < files.length) {
                        return files[i + 1];
                    }
                }
            }
        }
        return null; // End of pack
    }

    public static void saveStars(User user, File currentFile, int stars) {
        if(currentFile == null || user == null) {
            return;
        }

        // --- UPDATED LOGIC ---
        // We ALLOW "Guest" to setStars (so they can unlock the next level in memory)
        // We just rely on UserManager not having the Guest in its list to prevent disk writing.

        int currentBest = user.getStars(currentFile.getName());
        if(stars > currentBest) {
            user.setStars(currentFile.getName(), stars);

            // Only save to disk if it's a real registered user
            if(!"Guest".equalsIgnoreCase(user.getUsername())) {
                UserManager.getInstance().saveUsers();
            }
        }
    }
}
