package com.monkey.logic;

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

            if (line.startsWith("for") || line.startsWith("while")) {
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
                    int loops = parseForLoop(line);
                    for (int k = 0; k < loops; k++) {
                        if (!isRunning) break;
                        count += executeBlock(innerBlock, vars);
                    }
                } else if (line.startsWith("while")) {
                    while (ConditionEvaluator.evaluateCondition(processor.getEngine(), line, vars)) {
                        if (!isRunning) break;
                        count += executeBlock(innerBlock, vars);
                    }
                }

                i = endIndex;
                continue;
            }

            if ((line.startsWith("int ") || line.contains("=")) && !line.contains("==") && !line.contains("<=") && !line.contains(">=")) {
                assignVariable(line, vars);
                continue;
            }

            boolean success = processor.process(line, vars);
            if (success) {
                count++;
                Thread.sleep(200);
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
        else if (line.contains("//=")) operator = "//=";
        else if (line.contains("/=")) operator = "/=";
        else if (line.contains("%=")) operator = "%=";

        int opIndex = line.indexOf(operator);
        if (opIndex != -1) {
            String varName = line.substring(0, opIndex).trim();
            String expr = line.substring(opIndex + operator.length()).trim();

            int evalResult = MathEvaluator.evaluateMath(processor.getEngine(), expr, vars);

            if (operator.equals("=")) {
                vars.put(varName, evalResult);
            } else {
                int currentVal = MathEvaluator.getVal(processor.getEngine(), varName, vars);
                switch(operator) {
                    case "+=": vars.put(varName, currentVal + evalResult); break;
                    case "-=": vars.put(varName, currentVal - evalResult); break;
                    case "*=": vars.put(varName, currentVal * evalResult); break;
                    case "/=":
                    case "//=":
                        if (evalResult != 0) vars.put(varName, currentVal / evalResult);
                        break;
                    case "%=":
                        if (evalResult != 0) vars.put(varName, currentVal % evalResult);
                        break;
                }
            }
        }
    }

    private int parseForLoop(String line) {
        try {
            String[] parts = line.split(";");
            if (parts.length >= 2) {
                String cond = parts[1].trim();
                String[] condParts = cond.split("<");
                if (condParts.length == 2) {
                    return Integer.parseInt(condParts[1].trim());
                }
            }
        } catch (Exception ignored) {}
        return 1;
    }
}
