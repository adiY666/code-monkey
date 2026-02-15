package com.monkey.input;

import com.monkey.core.GameObject;
import com.monkey.core.Turtle;
import com.monkey.gui.VisualMonkeyStudio;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class StudioMouseHandler extends MouseAdapter {

    private final VisualMonkeyStudio context;
    private Object relativeAnchor = null;

    public StudioMouseHandler(VisualMonkeyStudio context) {
        this.context = context;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        handleMouse(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(context.isEditMode()) {
            context.getEngine().updateGhost(context.getSelectedTool(), e.getX(), e.getY());
        }
        handleMouse(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(context.isEditMode()) {
            context.getEngine().updateGhost(context.getSelectedTool(), e.getX(), e.getY());
        }
    }

    private void handleMouse(MouseEvent e) {
        // 1. RULER Logic
        if(context.getEngine().getRulerMode() > 0) {
            if(SwingUtilities.isLeftMouseButton(e)) {
                context.getEngine().handleRulerClick(e.getX(), e.getY());
            } else {
                context.getEngine().setRulerMode(0);
            }
            return;
        }

        if(!context.isEditMode()) return;

        // 2. DELETE Logic (Right Click)
        if(SwingUtilities.isRightMouseButton(e)) {
            context.getEngine().removeObject(e.getX(), e.getY());
            context.getAutoScrollWrapper().updateMapSize();
            return;
        }

        // 3. TOOLS Logic
        String tool = context.getSelectedTool();
        boolean changed = false;

        if(tool.equals("Rotate")) {
            handleRotate(e.getX(), e.getY());
            changed = true;
        } else if(tool.equals("Relative")) {
            handleRelative(e.getX(), e.getY());
            changed = true;
        } else if(!tool.equals("none")) {
            context.getEngine().addObject(tool, e.getX(), e.getY());
            changed = true;
        }

        if(changed) context.getAutoScrollWrapper().updateMapSize();
    }

    private void handleRotate(int x, int y) {
        Object obj = context.getEngine().getGameObjectAt(x, y);
        if(obj != null) {
            String val = JOptionPane.showInputDialog(context, "Angle (0-360):");
            try {
                if(val != null) {
                    double a = Double.parseDouble(val);
                    if(obj instanceof Turtle) ((Turtle)obj).angle = a;
                    else if(obj.equals("Monkey")) context.getEngine().monkeyAngle = a;
                    context.getEngine().repaint();
                }
            } catch(Exception ex) {}
        }
    }

    private void handleRelative(int x, int y) {
        Object obj = context.getEngine().getGameObjectAt(x, y);
        if(obj == null) return;

        if(relativeAnchor == null) {
            relativeAnchor = obj;
            JOptionPane.showMessageDialog(context, "Anchor Selected! Now click Target.");
        } else {
            openRelativeDialog(relativeAnchor, obj);
            relativeAnchor = null;
        }
    }

    private void openRelativeDialog(Object anchor, Object target) {
        JPanel p = new JPanel(new GridLayout(2, 2));
        JTextField d = new JTextField("100");
        JTextField a = new JTextField("0");
        p.add(new JLabel("Dist:")); p.add(d);
        p.add(new JLabel("Ang:")); p.add(a);

        if(JOptionPane.showConfirmDialog(context, p, "Set Relative", JOptionPane.OK_CANCEL_OPTION) == 0) {
            try {
                double dist = Double.parseDouble(d.getText());
                double ang = Math.toRadians(Double.parseDouble(a.getText()));
                double ax = (anchor instanceof GameObject) ? ((GameObject)anchor).x : context.getEngine().monkeyX;
                double ay = (anchor instanceof GameObject) ? ((GameObject)anchor).y : context.getEngine().monkeyY;

                double nx = ax + dist * Math.cos(ang);
                double ny = ay - dist * Math.sin(ang);

                if(target instanceof GameObject) { ((GameObject)target).x = nx; ((GameObject)target).y = ny; }
                else { context.getEngine().monkeyX = nx; context.getEngine().monkeyY = ny; }

                context.getEngine().repaint();
                context.getAutoScrollWrapper().updateMapSize();
            } catch(Exception e) {}
        }
    }
}
