package com.monkey.gui;

import com.monkey.core.Banana;
import com.monkey.core.GameObject;
import com.monkey.core.IGameObject;
import com.monkey.core.River;
import com.monkey.core.Stone;
import com.monkey.core.Turtle;
import org.json.JSONArray;
import org.json.JSONObject;

public class MapEditorLogic {

    private final GameEnginePanel engine;

    public MapEditorLogic(GameEnginePanel engine) {
        this.engine = engine;
    }

    public void addObject(String type, int x, int y) {
        switch (type) {
            case "Banana" -> engine.bananas.add(new Banana(x, y));
            case "Stone" -> engine.stones.add(new Stone(x, y));
            case "River" -> engine.rivers.add(new River(x, y));
            case "Turtle" -> engine.turtles.add(new Turtle(x, y, engine.turtles.size(), 0));
            case "Spawn" -> {
                engine.monkeyX = x;
                engine.monkeyY = y;
                engine.spawnSet = true;
            }
        }
        engine.saveInitialState();
        engine.repaint();
    }

    public void removeObject(int x, int y) {
        engine.bananas.removeIf(o -> dist(o, x, y) < 20);
        engine.stones.removeIf(o -> dist(o, x, y) < 20);
        engine.rivers.removeIf(o -> dist(o, x, y) < 25);
        engine.turtles.removeIf(o -> dist(o, x, y) < 20);

        // Re-number turtles so their IDs stay ordered
        for(int i = 0; i < engine.turtles.size(); i++) engine.turtles.get(i).id = i;

        engine.saveInitialState();
        engine.repaint();
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
