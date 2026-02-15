package com.monkey.gui;

import com.monkey.core.GameObject;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

public class AutoScrollPanel extends JPanel implements Scrollable {

    private final GameEnginePanel engine;
    private boolean isEditorMode = false;

    public AutoScrollPanel(GameEnginePanel engine) {
        this.engine = engine;
        setLayout(null);
        add(engine);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateMapSize();
            }
        });
    }

    public void setEditorMode(boolean isEditor) {
        this.isEditorMode = isEditor;
        updateMapSize();
    }

    public void updateMapSize() {
        // 1. Find the Furthest Object (Any type)
        int maxObjX = 0;
        int maxObjY = 0;

        maxObjX = Math.max(maxObjX, checkMaxX(engine.bananas));
        maxObjY = Math.max(maxObjY, checkMaxY(engine.bananas));

        maxObjX = Math.max(maxObjX, checkMaxX(engine.stones));
        maxObjY = Math.max(maxObjY, checkMaxY(engine.stones));

        maxObjX = Math.max(maxObjX, checkMaxX(engine.rivers));
        maxObjY = Math.max(maxObjY, checkMaxY(engine.rivers));

        for(var t : engine.turtles) {
            maxObjX = Math.max(maxObjX, (int)t.x);
            maxObjY = Math.max(maxObjY, (int)t.y);
        }

        // 2. Find the Furthest Banana Specifically
        int maxBananaX = 0;
        int maxBananaY = 0;
        boolean hasBanana = !engine.bananas.isEmpty();

        if (hasBanana) {
            maxBananaX = checkMaxX(engine.bananas);
            maxBananaY = checkMaxY(engine.bananas);
        }

        // 3. Include Monkey Start Position
        maxObjX = Math.max(maxObjX, (int)engine.monkeyX);
        maxObjY = Math.max(maxObjY, (int)engine.monkeyY);

        // --- NEW PADDING LOGIC ---
        int contentW, contentH;

        if (isEditorMode) {
            // Editor: Always huge padding for infinite feel
            contentW = maxObjX + 2000;
            contentH = maxObjY + 2000;
        } else {
            // Player: Specific Rules
            if (hasBanana) {
                // If Bananas exist: Expand to (Banana + 150) OR (Furthest Object), whichever is larger
                contentW = Math.max(maxObjX + 50, maxBananaX + 150); // Small buffer for objects, large for banana
                contentH = Math.max(maxObjY + 50, maxBananaY + 150);
            } else {
                // No Bananas: Snap "straight up after an object" (Small 50px buffer so it's not cut off)
                contentW = maxObjX + 50;
                contentH = maxObjY + 50;
            }
        }

        // 4. Handle Viewport (Disable scroll if fits screen)
        int viewW = 0;
        int viewH = 0;
        if (getParent() instanceof JViewport) {
            JViewport vp = (JViewport) getParent();
            viewW = vp.getWidth();
            viewH = vp.getHeight();
        }

        int finalW = Math.max(viewW, contentW);
        int finalH = Math.max(viewH, contentH);

        engine.setBounds(0, 0, finalW, finalH);
        setPreferredSize(new Dimension(finalW, finalH));
        revalidate();
    }

    private int checkMaxX(List<GameObject> list) {
        int max = 0;
        for(GameObject o : list) if(o.x > max) max = (int)o.x;
        return max;
    }

    private int checkMaxY(List<GameObject> list) {
        int max = 0;
        for(GameObject o : list) if(o.y > max) max = (int)o.y;
        return max;
    }

    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 50; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 200; }
    @Override public boolean getScrollableTracksViewportWidth() { return getPreferredSize().width <= getParent().getWidth(); }
    @Override public boolean getScrollableTracksViewportHeight() { return getPreferredSize().height <= getParent().getHeight(); }
}
