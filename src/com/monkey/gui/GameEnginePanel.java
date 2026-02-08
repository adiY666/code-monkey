package com.monkey.gui;

import com.monkey.core.GameObject;
import com.monkey.core.Turtle;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.json.JSONArray;
import org.json.JSONObject;

public class GameEnginePanel extends JPanel {

    public static final int GRID_SIZE = 50;
    public static final int GRID_OFFSET = 25;

    private static final int ANIMATION_DELAY = 20;
    private static final double MOVEMENT_SPEED = 1.0;

    private static final Color BG_COLOR = new Color(162, 217, 141);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Font GUI_FONT = new Font("Arial", Font.BOLD, 16);

    private static final Color MONKEY_BODY = new Color(139, 69, 19);
    private static final Color MONKEY_FACE = new Color(210, 180, 140);

    public double monkeyX = 350;
    public double monkeyY = 300;
    public double monkeyAngle = 0;
    public int levelLimit = 0;

    public final RulerTool ruler;

    // Ghost / Preview Variables
    private String ghostType = "none";
    private int ghostX = -100;
    private int ghostY = -100;

    public List<GameObject> bananas = new ArrayList<>();
    public List<GameObject> stones = new ArrayList<>();
    public List<GameObject> rivers = new ArrayList<>();
    public List<Turtle> turtles = new ArrayList<>();

    public GameEnginePanel() {
        setBackground(BG_COLOR);
        this.ruler = new RulerTool(this);
    }

    // --- GHOST UPDATE METHOD ---
    public void updateGhost(String type, int mouseX, int mouseY) {
        this.ghostType = type;
        // Snap to grid for preview
        this.ghostX = (mouseX / GRID_SIZE) * GRID_SIZE + GRID_OFFSET;
        this.ghostY = (mouseY / GRID_SIZE) * GRID_SIZE + GRID_OFFSET;
        repaint();
    }

    public void animateMove(double totalDistance, Runnable onFinish) {
        double rad = Math.toRadians(monkeyAngle);
        double dirX = Math.cos(rad);
        double dirY = -Math.sin(rad);

        final double targetDist = Math.abs(totalDistance);
        final int direction = (totalDistance < 0) ? -1 : 1;

        Timer timer = new Timer(ANIMATION_DELAY, null);

        timer.addActionListener(new ActionListener() {
            double covered = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                double step = Math.min(MOVEMENT_SPEED, targetDist - covered);
                monkeyX += step * dirX * direction;
                monkeyY += step * dirY * direction;
                covered += step;
                checkCollisions();
                repaint();
                if (covered >= targetDist) {
                    ((Timer)e.getSource()).stop();
                    if (onFinish != null) onFinish.run();
                }
            }
        });
        timer.start();
    }

    public void moveMonkey(double d) {
        double rad = Math.toRadians(monkeyAngle);
        monkeyX += d * Math.cos(rad);
        monkeyY -= d * Math.sin(rad);
        checkCollisions();
        repaint();
    }

    private void checkCollisions() {
        bananas.removeIf(b -> Math.hypot(b.x - monkeyX, b.y - monkeyY) < 25);
    }

    public void rotateMonkey(String dir) {
        monkeyAngle += dir.equals("left") ? 90 : -90;
        repaint();
    }

    public Object getGameObjectAt(int x, int y) {
        int clickRadius = 25;
        if(Math.hypot(x - monkeyX, y - monkeyY) < clickRadius) return "Monkey";
        for(GameObject t : turtles) if(Math.hypot(t.x - x, t.y - y) < clickRadius) return t;
        for(GameObject b : bananas) if(Math.hypot(b.x - x, b.y - y) < clickRadius) return b;
        for(GameObject s : stones) if(Math.hypot(s.x - x, s.y - y) < clickRadius) return s;
        for(GameObject r : rivers) if(Math.hypot(r.x - x, r.y - y) < clickRadius) return r;
        return null;
    }

    public void addObject(String type, int x, int y) {
        int gridX = (x / GRID_SIZE) * GRID_SIZE + GRID_OFFSET;
        int gridY = (y / GRID_SIZE) * GRID_SIZE + GRID_OFFSET;

        if(isOccupiedBySame(type, gridX, gridY)) return;

        switch(type) {
            case "Banana": bananas.add(new GameObject(gridX, gridY)); break;
            case "Stone": stones.add(new GameObject(gridX, gridY)); break;
            case "River": rivers.add(new GameObject(gridX, gridY)); break;
            case "Turtle":
                if(isWater(gridX, gridY)) turtles.add(new Turtle(gridX, gridY, turtles.size(), 0));
                break;
            case "Spawn": monkeyX = gridX; monkeyY = gridY; break;
        }
        repaint();
    }

    public void setRulerMode(int mode) { ruler.setMode(mode); repaint(); }
    public void handleRulerClick(int x, int y) { ruler.handleClick(x, y); repaint(); }
    public int getRulerMode() { return ruler.getMode(); }

    private boolean isOccupiedBySame(String type, int x, int y) {
        return switch(type) {
            case "Banana" -> bananas.stream().anyMatch(o -> o.x == x && o.y == y);
            case "Stone" -> stones.stream().anyMatch(o -> o.x == x && o.y == y);
            case "River" -> rivers.stream().anyMatch(o -> o.x == x && o.y == y);
            case "Turtle" -> turtles.stream().anyMatch(o -> o.x == x && o.y == y);
            default -> false;
        };
    }

    private boolean isWater(int x, int y) {
        return rivers.stream().anyMatch(r -> r.x == x && r.y == y);
    }

    public void removeObject(int x, int y) {
        int gridX = (x / GRID_SIZE) * GRID_SIZE + GRID_OFFSET;
        int gridY = (y / GRID_SIZE) * GRID_SIZE + GRID_OFFSET;
        bananas.removeIf(b -> b.x == gridX && b.y == gridY);
        stones.removeIf(s -> s.x == gridX && s.y == gridY);
        rivers.removeIf(r -> r.x == gridX && r.y == gridY);
        turtles.removeIf(t -> t.x == gridX && t.y == gridY);
        repaint();
    }

    public int getBananaCount() { return bananas.size(); }

    public JSONObject getLayoutAsJson() {
        JSONObject layout = new JSONObject();
        layout.put("bananas", listToArr(bananas));
        layout.put("stones", listToArr(stones));
        layout.put("rivers", listToArr(rivers));
        JSONArray tArr = new JSONArray();
        for(Turtle t : turtles) {
            JSONArray tData = new JSONArray();
            tData.put(t.x).put(t.y).put(t.id).put(t.angle);
            tArr.put(tData);
        }
        layout.put("turtles", tArr);
        return layout;
    }

    private JSONArray listToArr(List<GameObject> list) {
        JSONArray arr = new JSONArray();
        for(GameObject o : list) arr.put(new JSONArray().put(o.x).put(o.y));
        return arr;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Objects
        g2.setColor(new Color(93, 173, 226));
        for(GameObject r : rivers) g2.fillRect((int) r.x - 25, (int) r.y - 25, 50, 50);
        g2.setColor(Color.GRAY);
        for(GameObject s : stones) g2.fillRect((int) s.x - 25, (int) s.y - 25, 50, 50);
        g2.setColor(Color.YELLOW);
        for(GameObject b : bananas) g2.fillOval((int) b.x - 12, (int) b.y - 12, 24, 24);
        for(Turtle t : turtles) t.draw(g2);

        g2.setColor(MONKEY_BODY);
        g2.fillOval((int) monkeyX - 15, (int) monkeyY - 15, 30, 30);
        double rad = Math.toRadians(monkeyAngle);
        double faceX = monkeyX + 10 * Math.cos(rad);
        double faceY = monkeyY - 10 * Math.sin(rad);
        g2.setColor(MONKEY_FACE);
        g2.fillOval((int) faceX - 8, (int) faceY - 8, 16, 16);

        // --- DRAW GHOST PREVIEW ---
        if (!ghostType.equals("none") && !ghostType.equals("Rotate") && !ghostType.equals("Relative")) {
            Composite original = g2.getComposite();
            // Set 50% transparency
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

            int gx = ghostX;
            int gy = ghostY;

            switch(ghostType) {
                case "Banana" -> { g2.setColor(Color.YELLOW); g2.fillOval(gx - 12, gy - 12, 24, 24); }
                case "Stone" -> { g2.setColor(Color.GRAY); g2.fillRect(gx - 25, gy - 25, 50, 50); }
                case "River" -> { g2.setColor(Color.BLUE); g2.fillRect(gx - 25, gy - 25, 50, 50); }
                case "Turtle" -> { g2.setColor(new Color(34, 139, 34)); g2.fillOval(gx - 20, gy - 20, 40, 40); }
                case "Spawn" -> { g2.setColor(MONKEY_BODY); g2.fillOval(gx - 15, gy - 15, 30, 30); }
            }

            // Draw Outline
            g2.setColor(Color.WHITE);
            g2.drawRect(gx - 25, gy - 25, 50, 50); // Grid cell outline

            g2.setComposite(original); // Reset
        }

        ruler.draw(g2);

        g2.setColor(TEXT_COLOR);
        g2.setFont(GUI_FONT);
        String hud = "Bananas: " + bananas.size();
        if(levelLimit > 0) hud += " | Goal: " + levelLimit + " lines";

        int rMode = ruler.getMode();
        if(rMode == RulerTool.MODE_OBJECT) hud += " | RULER: OBJECT (Click 2 Items)";
        if(rMode == RulerTool.MODE_FREE) hud += " | RULER: FREE (Click 2 Points)";

        g2.drawString(hud, 20, 30);
    }
}
