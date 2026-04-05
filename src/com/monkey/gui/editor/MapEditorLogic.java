package com.monkey.gui.editor;

import com.monkey.core.GameObject;
import com.monkey.core.IGameObject;
import com.monkey.core.Turtle;
import com.monkey.gui.game.GameEnginePanel;
import com.monkey.level.LevelEditor; // Import the LevelEditor to access takeSnapshot()
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapEditorLogic {

    private final GameEnginePanel engine;
    private LevelEditor levelEditor; // We need a reference to the editor!

    private String selectedTool = "none";

    public MapEditorLogic(GameEnginePanel engine) {
        this.engine = engine;
        setupMouseListener();
    }

    public void setLevelEditor(LevelEditor editor) {
        this.levelEditor = editor;
    }

    public void setSelectedTool(String tool) {
        this.selectedTool = tool;
    }

    private void setupMouseListener() {
        engine.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (selectedTool == null || selectedTool.equals("none")) return;

                int x = e.getX();
                int y = e.getY();

                // --- THE UNDO TRACKER ---
                // Only take a snapshot if the levelEditor has been linked!
                if (levelEditor != null) {
                    levelEditor.takeSnapshot();
                }

                // --- APPLY THE CHANGES ---
                if (SwingUtilities.isLeftMouseButton(e)) {
                    addObject(selectedTool, x, y);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    removeObject(x, y);
                }

                engine.repaint();
            }
        });
    }

    public void addObject(String type, int x, int y) {
        // Your logic for adding a Banana, Stone, or Turtle to the lists
        System.out.println("Added " + type + " at " + x + ", " + y);
    }

    public void removeObject(int x, int y) {
        // Your logic for looping through lists and removing objects at X, Y
        System.out.println("Removed object at " + x + ", " + y);
    }

    public Object getGameObjectAt(int x, int y) {
        for(GameObject o : engine.bananas) if (dist(o, x, y) < 20) return o;
        for(GameObject o : engine.stones) if (dist(o, x, y) < 20) return o;
        for(Turtle o : engine.turtles) if (dist(o, x, y) < 20) return o;

        if (Math.hypot(engine.monkeyX - x, engine.monkeyY - y) < 20) return "Monkey";
        return null;
    }

    private double dist(IGameObject o, int x, int y) {
        return Math.hypot(o.getX() - x, o.getY() - y);
    }

    public JSONObject getLayoutAsJson() {
        JSONArray b = new JSONArray();
        for (GameObject o : engine.bananas) b.put(new JSONArray(new double[]{o.getX(), o.getY()}));

        JSONArray s = new JSONArray();
        for (GameObject o : engine.stones) s.put(new JSONArray(new double[]{o.getX(), o.getY()}));

        JSONArray r = new JSONArray();
        for (GameObject o : engine.rivers) r.put(new JSONArray(new double[]{o.getX(), o.getY()}));

        JSONArray t = new JSONArray();
        for (Turtle o : engine.turtles) t.put(new JSONArray(new double[]{o.getX(), o.getY(), (double)o.id, (double)o.type, o.angle}));

        JSONObject root = new JSONObject();
        root.put("bananas", b);
        root.put("stones", s);
        root.put("rivers", r);
        root.put("turtles", t);

        return root;
    }
}
