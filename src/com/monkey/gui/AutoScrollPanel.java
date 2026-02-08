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
        // Add listener to update size when window resizes
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
        int maxX = 0;
        int maxY = 0;

        // Calculate content bounds
        maxX = Math.max(maxX, checkMaxX(engine.bananas));
        maxY = Math.max(maxY, checkMaxY(engine.bananas));
        maxX = Math.max(maxX, checkMaxX(engine.stones));
        maxY = Math.max(maxY, checkMaxY(engine.stones));
        maxX = Math.max(maxX, checkMaxX(engine.rivers));
        maxY = Math.max(maxY, checkMaxY(engine.rivers));

        for(var t : engine.turtles) {
            maxX = Math.max(maxX, (int)t.x);
            maxY = Math.max(maxY, (int)t.y);
        }

        maxX = Math.max(maxX, (int)engine.monkeyX);
        maxY = Math.max(maxY, (int)engine.monkeyY);

        int padding = isEditorMode ? 2000 : 150;

        int contentW = maxX + padding;
        int contentH = maxY + padding;

        // --- NEW LOGIC: FIT TO SCREEN IF SMALLER ---
        int viewW = 0;
        int viewH = 0;

        if (getParent() instanceof JViewport) {
            JViewport vp = (JViewport) getParent();
            viewW = vp.getWidth();
            viewH = vp.getHeight();
        }

        // If content fits in the view, use view size (Scrollbars disappear)
        // If content is bigger, use content size (Scrollbars appear)
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

    // Scrollable implementation
    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 50; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 200; }

    // Return true if content is smaller than viewport to disable scrolling
    @Override public boolean getScrollableTracksViewportWidth() {
        return getPreferredSize().width <= getParent().getWidth();
    }
    @Override public boolean getScrollableTracksViewportHeight() {
        return getPreferredSize().height <= getParent().getHeight();
    }
}
