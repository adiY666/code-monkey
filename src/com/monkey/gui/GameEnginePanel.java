package com.monkey.gui;

import com.monkey.core.GameObject;
import com.monkey.core.Turtle;
import com.monkey.tools.RulerTool;
import com.monkey.tools.SightTool;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;

import org.json.JSONObject;

public class GameEnginePanel extends JPanel {

    // --- GAME STATE ---
    public double monkeyX = 350, monkeyY = 300;
    public double monkeyAngle = 0;

    public final List<GameObject> bananas = new ArrayList<>();
    public final List<GameObject> stones = new ArrayList<>();
    public final List<GameObject> rivers = new ArrayList<>();
    public final List<Turtle> turtles = new ArrayList<>();

    // --- TRACKING & RESET ---
    private double startX = 350, startY = 300, startAngle = 0;
    private final List<Turtle> startTurtles = new ArrayList<>();
    public boolean spawnSet = false;
    public int levelLimit = 0;
    private Runnable onLevelComplete;

    // --- TOOLS & UI STATE ---
    private Point currentMouse = new Point(0,0);
    private String ghostTool = "none";
    private int ghostX, ghostY;

    public final RulerTool rulerTool;
    public final SightTool sightTool;

    // --- SUB-SYSTEMS ---
    private final GameRenderer renderer;
    private final MapEditorLogic mapEditor;

    public GameEnginePanel() {
        setBackground(new Color(34, 139, 34));
        setFocusable(true);
        setPreferredSize(new Dimension(800, 600));

        this.rulerTool = new RulerTool(this);
        this.sightTool = new SightTool(this);

        // Initialize our separated modules!
        this.renderer = new GameRenderer(this);
        this.mapEditor = new MapEditorLogic(this);
    }

    // --- GETTERS ---
    public Point getCurrentMouse() { return currentMouse; }
    public String getGhostTool() { return ghostTool; }
    public int getGhostX() { return ghostX; }
    public int getGhostY() { return ghostY; }
    public int getBananaCount() { return bananas.size(); }

    // --- TOOL ACTIONS ---
    public void setOnLevelComplete(Runnable action) { this.onLevelComplete = action; }
    public void setRulerMode(int mode) { rulerTool.setMode(mode); repaint(); }
    public int getRulerMode() { return rulerTool.getMode(); }
    public void handleRulerClick(int x, int y) { rulerTool.handleClick(x, y); }
    public void updateMousePosition(Point p) { this.currentMouse = p; if(rulerTool.getMode() > 0) repaint(); }
    public void toggleSightTool() { sightTool.toggle(); repaint(); }

    public void checkCollisions() {
        Iterator<GameObject> it = bananas.iterator();
        while(it.hasNext()) {
            GameObject b = it.next();
            if(Math.hypot(b.x - monkeyX, b.y - monkeyY) < 40) {
                renderer.addPop(b.x, b.y); // Tell renderer to show effect
                it.remove();
                repaint();

                if(bananas.isEmpty() && onLevelComplete != null) {
                    onLevelComplete.run();
                }
            }
        }
    }

    public void resetLevel() {
        if (!spawnSet) {
            this.startX = monkeyX; this.startY = monkeyY; this.startAngle = monkeyAngle; spawnSet = true;
        }
        this.monkeyX = startX; this.monkeyY = startY; this.monkeyAngle = startAngle;
        turtles.clear();
        for(Turtle t : startTurtles) turtles.add(new Turtle(t.x, t.y, t.id, t.type, t.angle));
        renderer.clearEffects();
        repaint();
    }

    public void saveInitialState() {
        this.startX = monkeyX; this.startY = monkeyY; this.startAngle = monkeyAngle; this.spawnSet = true;
        startTurtles.clear();
        for(Turtle t : turtles) startTurtles.add(new Turtle(t.x, t.y, t.id, t.type, t.angle));
    }

    // --- DRAWING DELEGATION ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Let the renderer handle all the complex drawing!
        renderer.draw((Graphics2D) g);
    }

    // --- EDITOR DELEGATION ---
    public void updateGhost(String tool, int x, int y) {
        this.ghostTool = tool; this.ghostX = x; this.ghostY = y; repaint();
    }

    // Pass requests to MapEditorLogic
    public void addObject(String type, int x, int y) { mapEditor.addObject(type, x, y); }
    public void removeObject(int x, int y) { mapEditor.removeObject(x, y); }
    public Object getGameObjectAt(int x, int y) { return mapEditor.getGameObjectAt(x, y); }
    public JSONObject getLayoutAsJson() { return mapEditor.getLayoutAsJson(); }
}
