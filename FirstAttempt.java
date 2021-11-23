import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstAttempt {
    static HashMap<String, Integer> variables = new HashMap<String, Integer>();
    public static void main(String[] args) {
        try {
            String program = Files.readString(Path.of("Program1.txt"));
            parseStatements(program, "\\?");
        } catch (IOException e) {
            System.out.println("Error Finding File Path.");
            e.printStackTrace();
        }
    }

    static void parseStatements(String statementBlock, String delimiter) {
        String[] statements = statementBlock.split(delimiter);

            for (String statement: statements) {
                statement = statement.trim();
                Pattern prefix = Pattern.compile("var|loop|do|console|math");
                Matcher matcher = prefix.matcher(statement);

                if (matcher.find()) {
                    switch (matcher.group()) {
                        case "var":
                            dealWithVar(statement);
                            break;
                        case "loop":
                            dealWithLoop(statement);
                            break;
                        case "do":
                            dealWithConditional(statement);
                            break;
                        case "console":
                            dealWithPrint(statement);
                            break;
                        case "math":
                            dealWithMath(statement);
                            break;
                        case "exit":
                            dealWithExit(statement);
                            break;
                    }
                } else {
                    System.out.println("Not a valid statement: " + statement);
                    System.exit(1);
                }
            }
    }

    static void dealWithVar(String statement) {
        Pattern pattern = Pattern.compile("var [A-Z]+:");
        Matcher matcher = pattern.matcher(statement);
        if (matcher.find()) {
            pattern = Pattern.compile("^[0|1]+$");
            matcher = pattern.matcher(statement.split(":")[1]);
            if (matcher.find()) {
                String[] var = statement.split(" |:"); // name = var[1], val = var[2]
                variables.put(var[1], Integer.parseInt(var[2], 2)); // Convert from binary to decimal and add to dictionary
            } else {
                System.out.println("Variables can only be assigned to binary values: " + statement);
                System.exit(1);
            }
        } else {
            System.out.println("Variable names can only include [A-Z]: " + statement);
            System.exit(1);
        }
    }

    static void dealWithLoop(String statement) {
        Pattern prefix = Pattern.compile("loop<([A-Z]+|[0|1]+)->([A-Z]+|[0|1]+), ((math<(add|sub|mul|div|mod))\\|(op|[A-Z]+)\\|([A-Z]+|[0|1]+)>)>\\{([\\s\\S]+)\\}$");
        Matcher matcher = prefix.matcher(statement);
        if (matcher.matches()) {
            runLoop(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(8));
        } else {
            System.out.println("Error with loop declaration: " + statement);
            System.exit(1);
        }
    }

    static void dealWithPrint(String statement) {
        Pattern  pattern = Pattern.compile("console<.*>$");
        Matcher matcher = pattern.matcher(statement);
        if (matcher.find()) {
            Pattern val = Pattern.compile("[1|0]+");
            Pattern literal = Pattern.compile("'.*'");
            Pattern var = Pattern.compile("[A-Z]+");

            String[] printStatement = statement.split("<|>");
            if (printStatement.length > 1) { // Checks to see if trying to print nothing
                if (val.matcher(printStatement[1]).matches()) {
                    System.out.println(Integer.parseInt(printStatement[1], 2)); // Convert to binary and print
                } else if (literal.matcher(printStatement[1]).matches()) {
                    System.out.println(printStatement[1].substring(1, printStatement[1].length()-1)); // Print literal stripped of surrounding ''
                } else if (var.matcher(printStatement[1]).matches()) {
                    Integer varVal;
                    if ((varVal = variables.get(printStatement[1])) != null) {
                        System.out.println(varVal);
                    } else {
                        System.out.println("Unable to print variable that does not exist: " + statement);
                    }
                } else {
                    System.out.println("Can only output one variable, value, or string literal at a time: " + statement);
                    System.exit(1);
                }
            }
        } else {
            System.out.println("Must use console <> to print to console: " + statement);
            System.exit(1);
        }
    }

    static void dealWithConditional(String statement) {
        Pattern prefix = Pattern.compile("do <.*> if <.*> otherwise do <.*>$");
        Matcher matcher = prefix.matcher(statement);
        if (matcher.matches()) {
            String[] components = statement.split(" <|> |>$");
            String result;
            if ((result = parseCondition(components[3])).equals("true")) {
                parseStatements(components[1], ",");
            } else if (result.equals("false")) {
                parseStatements(components[5], ",");
            } else {
                System.out.println("Error during condition parse: " + components[3]);
                System.exit(1);
            }
        } else {
            System.out.println("Conditional must be done in the form do <.*> if <.*> otherwise do <.*>: " + statement);
            System.exit(1);
        }
    }

    static String parseCondition(String condition) {
        Pattern pattern = Pattern.compile("(.*?)(not)(.+)|(.+?)(and)(.+)|(.+?)(or)(.+)|(.+?)(!eq)(.+)|(.+?)(eq)(.+)|(.+?)(gt)(.+)|(.+?)(gt=)(.+)|(.+?)(lt)(.+)|(.+?)(lt=)(.+)|(true)|(false)");
        Matcher matcher = pattern.matcher(condition.trim());
        if (matcher.matches()) {
            if (matcher.group(2) != null) {
                String rightSide = parseCondition(matcher.group(3).trim());
                if (rightSide.equals("error")) {
                    return rightSide;
                }
                if (rightSide.equals("true")) {
                    return "false";
                } else {
                    return "true";
                }
            } else if (matcher.group(5) != null) {
                String rightSide = parseCondition(matcher.group(6).trim());
                String leftSide = parseCondition(matcher.group(4).trim());
                if (rightSide.equals("error") || leftSide.equals("error")) {
                    return "error";
                }
                if (leftSide.equals("false") || rightSide.equals("false")) {
                    return "false";
                } else {
                    return "true";
                }
            } else if (matcher.group(8) != null) {
                String rightSide = parseCondition(matcher.group(9).trim());
                String leftSide = parseCondition(matcher.group(7).trim());
                if (rightSide.equals("error") || leftSide.equals("error")) {
                    return "error";
                }
                if (leftSide.equals("true") || rightSide.equals("true")) {
                    return "true";
                } else {
                    return "false";
                }
            } else if (matcher.group(11) != null) {
                return evaluateExpression(matcher.group(10).trim(), matcher.group(11).trim(), matcher.group(12).trim());
            } else if (matcher.group(14) != null) {
                return evaluateExpression(matcher.group(13).trim(), matcher.group(14).trim(), matcher.group(15).trim());
            } else if (matcher.group(17) != null) {
                return evaluateExpression(matcher.group(16).trim(), matcher.group(17).trim(), matcher.group(18).trim());
            } else if (matcher.group(20) != null) {
                return evaluateExpression(matcher.group(19).trim(), matcher.group(20).trim(), matcher.group(21).trim());
            } else if (matcher.group(23) != null) {
                return evaluateExpression(matcher.group(22).trim(), matcher.group(23).trim(), matcher.group(24).trim());
            } else if (matcher.group(26) != null) {
                return evaluateExpression(matcher.group(25).trim(), matcher.group(26).trim(), matcher.group(27).trim());
            } else if (matcher.group(28) != null) {
                return "true";
            } else if (matcher.group(29) != null) {
                return "false";
            }
        }
        return "error";
    }

    static String evaluateExpression(String left, String operand, String right) {
        Integer leftValue;
        Integer rightValue;
        Pattern pattern = Pattern.compile("math<(add|sub|mul|div|mod)\\|([0|1]+|[A-Z]+)\\|([A-Z]+|[0|1]+)>$");
        Matcher leftMatcher = pattern.matcher(left);
        Matcher rightMatcher = pattern.matcher(right);
        if (leftMatcher.matches()) {
            leftValue = dealWithMath(left);
        } else {
            leftValue = getValue(left);
        }
        if (rightMatcher.matches()) {
            rightValue = dealWithMath(right);
        } else {
            rightValue = getValue(right);
        }
        if (leftValue == null || rightValue == null) {
            return "error";
        }
        switch (operand) {
            case "eq" :
                return leftValue == rightValue ? "true" : "false";
            case "!eq" :
                return leftValue != rightValue ? "true" : "false";
            case "gt" :
                return leftValue > rightValue ? "true" : "false";
            case "gt=" :
                return leftValue >= rightValue ? "true" : "false";
            case "lt" :
                return leftValue < rightValue ? "true" : "false";
            case "lt=" :
                return leftValue <= rightValue ? "true" : "false";
            default :
                return "error";
        }
    }

    static Integer getValue(String element) {
        Pattern pattern = Pattern.compile("([A-Z]+)|([0|1]+)");
        Matcher matcher = pattern.matcher(element.trim());
        if (matcher.matches()) {
            if (matcher.group(1) != null) {
                return variables.get(matcher.group(1));
            } else {
                return Integer.parseInt(matcher.group(2), 2);
            }
        }
        return null;
    }

    static void runLoop(String startVal, String endVal, String arithmetic, String statementBlock) {
        Pattern pattern = Pattern.compile("math<(add|sub|mul|div|mod)\\|(op|[A-Z]+)\\|([A-Z]+|[0|1]+)>$");
        Matcher matcher = pattern.matcher(arithmetic);
        Integer left;
        Integer right;
        if (matcher.matches()) {
            if (matcher.group(2).equals("op")) {
                left = Integer.parseInt(startVal, 2);
                right = getValue(endVal);
                if (right == null) {
                    System.out.println("Error, expected valid variable for loop iteration: " + endVal);
                    System.exit(1);
                }
                if (matcher.group(1).equals("add") || matcher.group(1).equals("mul")) {
                    while (left <= right) {
                        parseStatements(statementBlock, ",");
                        left = dealWithMath("math<"+matcher.group(1)+"|"+Integer.toBinaryString(left)+"|"+matcher.group(3)+">");
                    }
                } else {
                    while (left >= right) {
                        parseStatements(statementBlock, ",");
                        left = dealWithMath("math<"+matcher.group(1)+"|"+Integer.toBinaryString(left)+"|"+matcher.group(3)+">");
                    }
                }
            } else {
                left = variables.get(startVal);
                if (left == null) {
                    System.out.println("Error, expected valid variable for loop incrementation: " + startVal);
                    System.exit(1);
                }
                right = getValue(endVal);
                if (right == null) {
                    System.out.println("Error, expected valid variable for loop iteration: " + endVal);
                    System.exit(1);
                }
                if (matcher.group(1).equals("add") || matcher.group(1).equals("mul")) {
                    while (left <= right) {
                        parseStatements(statementBlock, ",");
                        if (startVal.equals(matcher.group(2))) {
                            left = dealWithMath(arithmetic);
                        }
                    }
                } else {
                    while (left >= right) {
                        parseStatements(statementBlock, ",");
                        if (startVal.equals(matcher.group(2))) {
                            left = dealWithMath(arithmetic);
                        }
                    }
                }
            }
        } else {
            System.out.println("Error with incrementation in loop: " + arithmetic);
            System.exit(1);
        }
    }

    static Integer dealWithMath(String statement) {
        Pattern pattern = Pattern.compile("math<(add|sub|mul|div|mod)\\|([A-Z]+|[0|1]+)\\|([A-Z]+|[0|1]+)>$");
        Matcher matcher = pattern.matcher(statement);
        if (matcher.matches()) {
                Integer leftSide;
                Integer rightSide;
                switch (matcher.group(1)) {
                    case "add":
                            leftSide = variables.get(matcher.group(2));
                            rightSide = getValue(matcher.group(3));
                            if (leftSide == null) {
                                leftSide = Integer.parseInt(matcher.group(2), 2);
                                return leftSide + rightSide;
                            } else {
                                variables.put(matcher.group(2), leftSide + rightSide);
                                return leftSide + rightSide;
                            }
                    case "sub":
                        leftSide = variables.get(matcher.group(2));
                        rightSide = getValue(matcher.group(3));
                        if (leftSide == null) {
                            leftSide = Integer.parseInt(matcher.group(2), 2);
                            return leftSide - rightSide;
                        } else {
                            variables.put(matcher.group(2), leftSide - rightSide);
                            return leftSide - rightSide;
                        }
                    case "mul":
                        leftSide = variables.get(matcher.group(2));
                        rightSide = getValue(matcher.group(3));
                        if (leftSide == null) {
                            leftSide = Integer.parseInt(matcher.group(2), 2);
                            return leftSide * rightSide;
                        } else {
                            variables.put(matcher.group(2), leftSide * rightSide);
                            return leftSide * rightSide;
                        }
                    case "div":
                        leftSide = variables.get(matcher.group(2));
                        rightSide = getValue(matcher.group(3));
                        if (leftSide == null) {
                            leftSide = Integer.parseInt(matcher.group(2), 2);
                            return leftSide / rightSide;
                        } else {
                            variables.put(matcher.group(2), leftSide / rightSide);
                            return leftSide / rightSide;
                        }
                    case "mod":
                        leftSide = variables.get(matcher.group(2));
                        rightSide = getValue(matcher.group(3));
                        if (leftSide == null) {
                            leftSide = Integer.parseInt(matcher.group(2), 2);
                            return leftSide % rightSide;
                        } else {
                            variables.put(matcher.group(2), leftSide % rightSide);
                            return leftSide % rightSide;
                        }
                    default:
                        System.out.println("Error with arithmetic: " + statement);
                        System.exit(1);
                        return null;
                }
        } else {
            System.out.println("Error with arithmetic: " + statement);
            System.exit(1);
            return null;
        }
    }

    static void dealWithExit(String statement) {
        if (statement.equals("exit")) {
            System.exit(0);
        }
    }
}