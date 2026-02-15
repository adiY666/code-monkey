package com.monkey.logic;

import com.monkey.core.GameObject;
import com.monkey.core.Turtle;
import com.monkey.gui.GameEnginePanel;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class LevelLoader {

    public static final String JSON_EXT = ".json";

    // JSON Keys
    private static final String KEY_LAYOUT = "layout";
    private static final String KEY_START_X = "start_x";
    private static final String KEY_START_Y = "start_y";
    private static final String KEY_START_ANGLE = "start_angle";
    private static final String KEY_TARGET_LINES = "target_lines";
    private static final String KEY_SORT_INDEX = "sort_index";
    private static final String KEY_CATEGORY = "category";

    public static void loadLevel(File f, GameEnginePanel engine) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(f.getPath())));
        JSONObject json = new JSONObject(content);

        engine.bananas.clear(); engine.stones.clear();
        engine.rivers.clear(); engine.turtles.clear();

        engine.monkeyX = json.optDouble(KEY_START_X, 350);
        engine.monkeyY = json.optDouble(KEY_START_Y, 300);
        engine.monkeyAngle = json.optDouble(KEY_START_ANGLE, 0);
        engine.levelLimit = json.optInt(KEY_TARGET_LINES, 5);

        JSONObject layout = json.getJSONObject(KEY_LAYOUT);
        parseObj(layout.getJSONArray("bananas"), engine.bananas);
        parseObj(layout.getJSONArray("stones"), engine.stones);
        parseObj(layout.getJSONArray("rivers"), engine.rivers);

        JSONArray tArr = layout.getJSONArray("turtles");
        for(int i = 0; i < tArr.length(); i++) {
            JSONArray t = tArr.getJSONArray(i);
            engine.turtles.add(new Turtle(t.getDouble(0), t.getDouble(1), t.getInt(2), t.getDouble(3)));
        }
    }

    public static void saveLevel(File file, GameEnginePanel engine, String category, int sortIndex) throws Exception {
        JSONObject root = new JSONObject();
        root.put(KEY_LAYOUT, engine.getLayoutAsJson());
        root.put(KEY_START_X, engine.monkeyX);
        root.put(KEY_START_Y, engine.monkeyY);
        root.put(KEY_START_ANGLE, engine.monkeyAngle);
        root.put(KEY_TARGET_LINES, engine.levelLimit);
        root.put(KEY_SORT_INDEX, sortIndex);
        root.put(KEY_CATEGORY, category);

        try(FileWriter fw = new FileWriter(file)) {
            fw.write(root.toString(4));
        }
    }

    // Helper to get meta-data without loading the whole visual map
    public static JSONObject readMetadata(File f) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(f.getPath())));
        return new JSONObject(content);
    }

    private static void parseObj(JSONArray arr, List<GameObject> list) {
        for(int i = 0; i < arr.length(); i++)
            list.add(new GameObject(arr.getJSONArray(i).getDouble(0), arr.getJSONArray(i).getDouble(1)));
    }

    public static List<File> getAllLevels() {
        List<File> list = new ArrayList<>();
        scanFolder(new File("levels/basics"), list);
        scanFolder(new File("levels/loops"), list);
        scanFolder(new File("levels/vars"), list);
        scanFolder(new File("levels/advanced"), list);
        scanFolder(new File("levels/custom"), list);
        return list;
    }

    private static void scanFolder(File dir, List<File> list) {
        if(dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(JSON_EXT));
            if(files != null) {
                Arrays.sort(files);
                for(File f : files) list.add(f);
            }
        }
    }
}
