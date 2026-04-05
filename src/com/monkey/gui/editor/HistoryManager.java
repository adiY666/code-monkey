package com.monkey.gui.editor;

import java.util.Stack;
import org.json.JSONObject;

public class HistoryManager {

    private static final int MAX_HISTORY = 50;

    private final Stack<String> undoStack = new Stack<>();
    private final Stack<String> redoStack = new Stack<>();

    public void saveSnapshot(JSONObject currentState) {
        undoStack.push(currentState.toString());

        if (undoStack.size() > MAX_HISTORY) {
            undoStack.remove(0); // Remove the oldest memory if we exceed the limit
        }

        redoStack.clear(); // Any new action erases the "redo" future
    }

    public JSONObject undo(JSONObject currentState) {
        if (undoStack.isEmpty()) return null;

        redoStack.push(currentState.toString());
        return new JSONObject(undoStack.pop());
    }

    public JSONObject redo(JSONObject currentState) {
        if (redoStack.isEmpty()) return null;

        undoStack.push(currentState.toString());
        return new JSONObject(redoStack.pop());
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }
}
