package com.monkey.logic;

import com.monkey.gui.GameEnginePanel;

import java.util.*;
import java.util.function.IntConsumer;
import javax.swing.SwingUtilities;

public class CodeExecutor {

    private final GameEnginePanel engine;
    private final IntConsumer onComplete;
    private volatile boolean isRunning = false;
    private Thread executionThread;
    private Interpreter interpreter;

    public CodeExecutor(GameEnginePanel engine, IntConsumer onComplete) {
        this.engine = engine;
        this.onComplete = onComplete;
    }

    public void execute(String code) {
        if (isRunning) return;
        isRunning = true;
        engine.resetLevel();

        CommandProcessor processor = new CommandProcessor(engine);
        interpreter = new Interpreter(processor);

        executionThread = new Thread(() -> {
            boolean completedNaturally = true;
            int totalLinesExecuted = 0;

            try {
                String[] linesArr = code.split("\n");
                List<String> lines = new ArrayList<>();
                Collections.addAll(lines, linesArr);

                Map<String, Integer> variables = new HashMap<>();

                totalLinesExecuted = interpreter.executeBlock(lines, variables);

            } catch (InterruptedException e) {
                completedNaturally = false;
            } catch (Exception e) {
                System.err.println("Execution Error: " + e.getMessage());
            }

            isRunning = false;

            if (completedNaturally && interpreter.isRunning()) {
                final int finalCount = totalLinesExecuted;
                SwingUtilities.invokeLater(() -> onComplete.accept(finalCount));
            }
        });

        executionThread.start();
    }

    public void stop() {
        isRunning = false;
        if (interpreter != null) {
            interpreter.stop();
        }
        if (executionThread != null && executionThread.isAlive()) {
            executionThread.interrupt();
        }
    }
}
