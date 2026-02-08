package com.monkey.logic;

import com.monkey.gui.GameEnginePanel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;

public class CodeExecutor {

    private static final int ANIMATION_FRAMES = 30;
    private static final int FRAME_DELAY = 16;
    private static final int COMMAND_DELAY = 200;

    private final GameEnginePanel engine;
    private final Consumer<Integer> onComplete;

    public CodeExecutor(GameEnginePanel engine, Consumer<Integer> onComplete) {
        this.engine = engine;
        this.onComplete = onComplete;
    }

    public void execute(String code) {
        new Thread(() -> {
            try {
                String[] rawLines = code.split("\n");
                List<String> lines = new ArrayList<>();
                int validLineCount = 0;
                for(String s : rawLines) {
                    if(!s.trim().isEmpty() && !s.trim().startsWith("#")) {
                        lines.add(s.trim());
                        validLineCount++;
                    }
                }

                runBlock(lines);

                final int linesUsed = validLineCount;
                SwingUtilities.invokeLater(() -> onComplete.accept(linesUsed));

            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runBlock(List<String> lines) throws InterruptedException {
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if(line.startsWith("step")) {
                int val = parseNumber(line);
                animateMove(val);
                Thread.sleep(COMMAND_DELAY);
            } else if(line.startsWith("turn")) {
                String dir = line.contains("left") ? "left" : "right";
                SwingUtilities.invokeLater(() -> engine.rotateMonkey(dir));
                Thread.sleep(COMMAND_DELAY);
            } else if(line.startsWith("for")) {
                int limit = 0;
                Matcher m = Pattern.compile("i\\s*<\\s*(\\d+)").matcher(line);
                if(m.find()) limit = Integer.parseInt(m.group(1));

                List<String> block = new ArrayList<>();
                int openBraces = 1;
                i++;
                while(i < lines.size() && openBraces > 0) {
                    String sub = lines.get(i);
                    if(sub.contains("{")) openBraces++;
                    if(sub.contains("}")) openBraces--;
                    if(openBraces > 0) block.add(sub);
                    if(openBraces > 0) i++;
                }

                for(int k = 0; k < limit; k++) {
                    runBlock(block);
                }
            }
        }
    }

    private void animateMove(int distance) throws InterruptedException {
        double perFrame = (double) distance / ANIMATION_FRAMES;
        for(int i = 0; i < ANIMATION_FRAMES; i++) {
            SwingUtilities.invokeLater(() -> engine.moveMonkey(perFrame));
            Thread.sleep(FRAME_DELAY);
        }
    }

    private int parseNumber(String line) {
        Matcher m = Pattern.compile("-?\\d+").matcher(line);
        return m.find() ? Integer.parseInt(m.group()) : 0;
    }

}
