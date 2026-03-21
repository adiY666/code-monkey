package com.monkey.interpreter;

import java.util.List;
import java.util.Map;

public class Interpreter {

    private final CommandProcessor processor;
    private volatile boolean isRunning = true;

    public Interpreter(CommandProcessor processor) {
        this.processor = processor;
    }

    public void stop() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public int executeBlock(List<String> lines, Map<String, Integer> vars) throws InterruptedException {
        int count = 0;

        for (int i = 0; i < lines.size(); i++) {
            if (!isRunning) break;

            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue;

            // --- Loop Logic (For/While) ---
            if (line.startsWith("for") || line.startsWith("while") || line.startsWith("if")) {
                int openBraces = 0;
                if (line.contains("{")) openBraces++;

                int endIndex = i;
                for (int j = i + 1; j < lines.size(); j++) {
                    String innerLine = lines.get(j);
                    if (innerLine.contains("{")) openBraces++;
                    if (innerLine.contains("}")) openBraces--;

                    if (openBraces == 0) {
                        endIndex = j;
                        break;
                    }
                }

                List<String> innerBlock = lines.subList(i + 1, endIndex);

                if (line.startsWith("for")) {
                    Object[] loopData = parseForLoopData(line, vars);
                    String varName = (String) loopData[0];
                    int currentVal = (int) loopData[1];
                    String condOp = (String) loopData[2];
                    int targetVal = (int) loopData[3];
                    int stepVal = (int) loopData[4];

                    Integer previousVal = vars.get(varName);

                    while (true) {
                        if (!isRunning) break;

                        boolean conditionMet = false;
                        if (condOp.equals("<") && currentVal < targetVal) conditionMet = true;
                        else if (condOp.equals("<=") && currentVal <= targetVal) conditionMet = true;
                        else if (condOp.equals(">") && currentVal > targetVal) conditionMet = true;
                        else if (condOp.equals(">=") && currentVal >= targetVal) conditionMet = true;

                        if (!conditionMet) break;

                        vars.put(varName, currentVal);
                        count += executeBlock(innerBlock, vars);
                        currentVal += stepVal;
                    }

                    if (previousVal != null) vars.put(varName, previousVal);
                    else vars.remove(varName);

                } else if (line.startsWith("while")) {
                    while (ConditionEvaluator.evaluateCondition(processor.getEngine(), line, vars)) {
                        if (!isRunning) break;
                        count += executeBlock(innerBlock, vars);
                    }
                }

                else if (line.startsWith("if")) {
                    // It uses the exact same ConditionEvaluator as the while loop!
                    if (ConditionEvaluator.evaluateCondition(processor.getEngine(), line, vars)) {
                        count += executeBlock(innerBlock, vars);
                    }
                }

                i = endIndex;
                continue;
            }

            // --- Variable Assignment ---
            if ((line.startsWith("int ") || line.contains("=")) && !line.contains("==") && !line.contains("<=") && !line.contains(">=")) {
                assignVariable(line, vars);
                continue;
            }

            // --- Command Execution & Animation Sync ---
            boolean success = processor.process(line, vars);
            if (success) {
                count++;

                // --- THE FIX: Wait for the visual "glide" to finish ---
                while (processor.getEngine().getAnimationManager().isAnimating()) {
                    if (!isRunning) break;
                    Thread.sleep(10); // Small check interval to prevent lag
                }

                // Tiny rest so the moves don't look robotic
                Thread.sleep(50);
            }
        }
        return count;
    }

    private void assignVariable(String line, Map<String, Integer> vars) throws InterruptedException {
        line = line.replace("int ", "").replace(";", "").trim();
        String operator = "=";
        if (line.contains("+=")) operator = "+=";
        else if (line.contains("-=")) operator = "-=";
        else if (line.contains("*=")) operator = "*=";
        else if (line.contains("/=")) operator = "/=";

        int opIndex = line.indexOf(operator);
        if (opIndex != -1) {
            String varName = line.substring(0, opIndex).trim();
            String expr = line.substring(opIndex + operator.length()).trim();
            int evalResult = MathEvaluator.evaluateMath(processor.getEngine(), expr, vars);

            if (operator.equals("=")) {
                vars.put(varName, evalResult);
            } else {
                int currentVal = vars.getOrDefault(varName, 0);
                if (operator.equals("+=")) vars.put(varName, currentVal + evalResult);
                else if (operator.equals("-=")) vars.put(varName, currentVal - evalResult);
                else if (operator.equals("*=")) vars.put(varName, currentVal * evalResult);
                else if (operator.equals("/=") && evalResult != 0) vars.put(varName, currentVal / evalResult);
            }
        }
    }

    private Object[] parseForLoopData(String line, Map<String, Integer> vars) {
        String varName = "i";
        int startVal = 0;
        String conditionOp = "<";
        int endVal = 1;
        int stepVal = 1;

        try {
            int openParen = line.indexOf('(');
            int closeParen = line.lastIndexOf(')');
            if (openParen != -1 && closeParen != -1) {
                String inner = line.substring(openParen + 1, closeParen);
                String[] parts = inner.split(";");

                if (parts.length >= 2) {
                    String init = parts[0].trim();
                    if (init.startsWith("int ")) init = init.substring(4).trim();
                    String[] initSplit = init.split("=");
                    varName = initSplit[0].trim();
                    startVal = MathEvaluator.evaluateMath(processor.getEngine(), initSplit[1].trim(), vars);

                    String cond = parts[1].trim();
                    if (cond.contains("<=")) { conditionOp = "<="; endVal = MathEvaluator.evaluateMath(processor.getEngine(), cond.split("<=")[1].trim(), vars); }
                    else if (cond.contains(">=")) { conditionOp = ">="; endVal = MathEvaluator.evaluateMath(processor.getEngine(), cond.split(">=")[1].trim(), vars); }
                    else if (cond.contains("<")) { conditionOp = "<"; endVal = MathEvaluator.evaluateMath(processor.getEngine(), cond.split("<")[1].trim(), vars); }
                    else if (cond.contains(">")) { conditionOp = ">"; endVal = MathEvaluator.evaluateMath(processor.getEngine(), cond.split(">")[1].trim(), vars); }

                    if (parts.length >= 3) {
                        String stepStr = parts[2].trim();
                        if (stepStr.contains("++")) stepVal = 1;
                        else if (stepStr.contains("--")) stepVal = -1;
                        else if (stepStr.contains("+=")) stepVal = MathEvaluator.evaluateMath(processor.getEngine(), stepStr.split("\\+=")[1].trim(), vars);
                        else if (stepStr.contains("-=")) stepVal = -MathEvaluator.evaluateMath(processor.getEngine(), stepStr.split("-=")[1].trim(), vars);
                    }
                }
            }
        } catch (Exception ignored) {}
        return new Object[]{varName, startVal, conditionOp, endVal, stepVal};
    }
}
