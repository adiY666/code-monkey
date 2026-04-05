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
        switch (type) {
            case "Banana":
                engine.bananas.add(new com.monkey.core.Banana(x, y));
                break;
            case "Stone":
                engine.stones.add(new com.monkey.core.Stone(x, y));
                break;
            case "River":
                engine.rivers.add(new com.monkey.core.River(x, y));
                break;
            case "Turtle":
                // Automatically assign the next available ID to the turtle (T0, T1, T2...)
                int newId = 0;
                for (Turtle t : engine.turtles) {
                    if (t.id >= newId) newId = t.id + 1;
                }
                engine.turtles.add(new Turtle(x, y, newId, 0, 0));
                break;
            case "Spawn":
                engine.monkeyX = x;
                engine.monkeyY = y;
                // Update the starting location so the monkey resets here!
                engine.saveInitialState();
                break;
        }
    }

    public void removeObject(int x, int y) {
        // Remove anything within 20 pixels of the right-click
        engine.bananas.removeIf(b -> Math.hypot(b.x - x, b.y - y) < 20);
        engine.stones.removeIf(s -> Math.hypot(s.x - x, s.y - y) < 20);
        engine.rivers.removeIf(r -> Math.hypot(r.x - x, r.y - y) < 20);
        engine.turtles.removeIf(t -> Math.hypot(t.x - x, t.y - y) < 20);
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
