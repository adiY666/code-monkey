package com.monkey.gui;

import com.monkey.core.GameObject;
import com.monkey.core.Turtle;
import com.monkey.design.EditorDesign;
import com.monkey.design.ItemDesign;
import com.monkey.design.MonkeyDesign;
import com.monkey.design.TerrainDesign;
import com.monkey.tools.RulerTool; // Import the new tool

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.json.JSONArray;
import org.json.JSONObject;

public class GameEnginePanel extends JPanel {

    // --- GAME OBJECTS ---
    public double monkeyX = 350, monkeyY = 300;
    public double monkeyAngle = 0;

    public final List<GameObject> bananas = new ArrayList<>();
    public final List<GameObject> stones = new ArrayList<>();
    public final List<GameObject> rivers = new ArrayList<>();
    public final List<Turtle> turtles = new ArrayList<>();

    // --- VISUAL EFFECTS ---
    private final List<PopEffect> effects = new ArrayList<>();
    private Point currentMouse = new Point(0,0);

    // --- RESET STATE ---
    private double startX = 350, startY = 300, startAngle = 0;
    private final List<Turtle> startTurtles = new ArrayList<>();
    private boolean spawnSet = false;

    // --- TOOLS ---
    private String ghostTool = "none";
    private int ghostX, ghostY;

    public final RulerTool rulerTool;
    public int levelLimit = 0;

    public GameEnginePanel() {
        setBackground(new Color(34, 139, 34));
        setFocusable(true);
        setPreferredSize(new Dimension(800, 600));

        // Initialize Tool
        this.rulerTool = new RulerTool(this);

        // 1. TRACK MOUSE
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                currentMouse = e.getPoint();
                if(rulerTool.getMode() > 0) repaint();

                if(!ghostTool.equals("none")) {
                    updateGhost(ghostTool, e.getX(), e.getY());
                }
            }
        });

        // 2. ANIMATION LOOP
        new Timer(30, e -> updateEffects()).start();
    }

    // --- COMPATIBILITY METHODS (Fixes the Error) ---
    public void setRulerMode(int mode) {
        rulerTool.setMode(mode);
        repaint();
    }

    public int getRulerMode() {
        return rulerTool.getMode();
    }

    public void handleRulerClick(int x, int y) {
        rulerTool.handleClick(x, y);
    }

    // --- LOGIC ---

    public void step(double dist) {
        double rad = Math.toRadians(monkeyAngle);
        monkeyX += dist * Math.cos(rad);
        monkeyY -= dist * Math.sin(rad);
        repaint();
    }

    public void rotateMonkey(String dir) {
        if(dir.equals("left")) monkeyAngle += 90;
        else monkeyAngle -= 90;
        repaint();
    }

    public void animateMove(int distance, Runnable onEnd) {
        final int frames = 10;
        final double stepPerFrame = (double) distance / frames;

        new Timer(20, new java.awt.event.ActionListener() {
            int count = 0;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                step(stepPerFrame);
                checkCollisions();
                count++;
                if (count >= frames) {
                    ((Timer)e.getSource()).stop();
                    if(onEnd != null) onEnd.run();
                }
            }
        }).start();
    }

    public void checkCollisions() {
        Iterator<GameObject> it = bananas.iterator();
        while(it.hasNext()) {
            GameObject b = it.next();
            if(Math.hypot(b.x - monkeyX, b.y - monkeyY) < 40) {
                effects.add(new PopEffect(b.x, b.y));
                it.remove();
                repaint();
            }
        }
    }

    private void updateEffects() {
        if(effects.isEmpty()) return;
        Iterator<PopEffect> it = effects.iterator();
        while(it.hasNext()) {
            PopEffect p = it.next();
            p.life--;
            p.radius += 2;
            if(p.life <= 0) it.remove();
        }
        repaint();
    }

    public void resetLevel() {
        if (!spawnSet) {
            this.startX = monkeyX; this.startY = monkeyY; this.startAngle = monkeyAngle; spawnSet = true;
        }
        this.monkeyX = startX; this.monkeyY = startY; this.monkeyAngle = startAngle;
        turtles.clear();
        for(Turtle t : startTurtles) turtles.add(new Turtle(t.x, t.y, t.id, t.type, t.angle));
        effects.clear();
        repaint();
    }

    public void saveInitialState() {
        this.startX = monkeyX; this.startY = monkeyY; this.startAngle = monkeyAngle; this.spawnSet = true;
        startTurtles.clear(); for(Turtle t : turtles) startTurtles.add(new Turtle(t.x, t.y, t.id, t.type, t.angle));
    }

    // --- DRAWING ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        TerrainDesign.drawRivers(g2, rivers);
        TerrainDesign.drawStones(g2, stones);

        for(Turtle t : turtles) t.draw(g2);
        for(GameObject b : bananas) ItemDesign.drawBanana(g2, b.x, b.y);

        MonkeyDesign.draw(g2, monkeyX, monkeyY, monkeyAngle);

        for(PopEffect p : effects) {
            g2.setColor(new Color(255, 255, 0, Math.max(0, p.life * 10)));
            g2.setStroke(new BasicStroke(3));
            g2.drawOval((int)p.x - p.radius, (int)p.y - p.radius, p.radius * 2, p.radius * 2);
        }

        if(!ghostTool.equals("none")) EditorDesign.drawGhost(g2, ghostTool, ghostX, ghostY);

        // DELEGATE RULER DRAWING
        rulerTool.draw(g2, currentMouse);
    }

    // --- EDITOR HELPERS ---

    public void updateGhost(String tool, int x, int y) {
        this.ghostTool = tool; this.ghostX = x; this.ghostY = y; repaint();
    }

    public void addObject(String type, int x, int y) {
        if(type.equals("Banana")) bananas.add(new GameObject(x, y));
        else if(type.equals("Stone")) stones.add(new GameObject(x, y));
        else if(type.equals("River")) rivers.add(new GameObject(x, y));
        else if(type.equals("Turtle")) turtles.add(new Turtle(x, y, turtles.size(), 0));
        else if(type.equals("Spawn")) { monkeyX = x; monkeyY = y; spawnSet = true; }
        saveInitialState(); repaint();
    }

    public void removeObject(int x, int y) {
        bananas.removeIf(o -> dist(o, x, y) < 20);
        stones.removeIf(o -> dist(o, x, y) < 20);
        rivers.removeIf(o -> dist(o, x, y) < 25);
        turtles.removeIf(o -> dist(o, x, y) < 20);
        for(int i=0; i<turtles.size(); i++) turtles.get(i).id = i;
        saveInitialState(); repaint();
    }

    public GameObject getGameObjectAt(int x, int y) {
        for(GameObject o : bananas) if(dist(o,x,y)<20) return o;
        for(GameObject o : stones) if(dist(o,x,y)<20) return o;
        for(GameObject o : turtles) if(dist(o,x,y)<20) return o;
        if(Math.hypot(monkeyX-x, monkeyY-y) < 20) return new GameObject(monkeyX, monkeyY);
        return null;
    }

    private double dist(GameObject o, int x, int y) { return Math.hypot(o.x - x, o.y - y); }
    public int getBananaCount() { return bananas.size(); }

    public JSONObject getLayoutAsJson() {
        JSONArray b = new JSONArray(); for(GameObject o : bananas) b.put(new JSONArray(new double[]{o.x, o.y}));
        JSONArray s = new JSONArray(); for(GameObject o : stones) s.put(new JSONArray(new double[]{o.x, o.y}));
        JSONArray r = new JSONArray(); for(GameObject o : rivers) r.put(new JSONArray(new double[]{o.x, o.y}));
        JSONArray t = new JSONArray(); for(Turtle o : turtles) t.put(new JSONArray(new double[]{o.x, o.y, (double)o.id, (double)o.type, o.angle}));
        JSONObject root = new JSONObject();
        root.put("bananas", b); root.put("stones", s); root.put("rivers", r); root.put("turtles", t);
        return root;
    }

    private static class PopEffect {
        double x, y; int radius = 5; int life = 20;
        PopEffect(double x, double y) { this.x = x; this.y = y; }
    }
}
