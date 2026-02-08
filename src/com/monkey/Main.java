package com.monkey;

import com.monkey.gui.LevelMenu;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LevelMenu().setVisible(true));
    }
}
