import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {
    static HashMap<String, Integer> variables = new HashMap<String, Integer>();
    public static void main(String[] args) {
        try {
            String program = Files.readString(Path.of("conditional.txt"));
            String[] statements = program.split("\\?");

            for (String statement: statements) {
                Pattern prefix = Pattern.compile("var|loop|do|console");
                Matcher matcher = prefix.matcher(statement);

                if (matcher.find()) {
                    switch (matcher.group()) { // TODO CHECK FOR ERRORS THROUGH RETURN AND EXIT OR EXIT IN FUNCTIONS?
                        case "var":
                            dealWithVar(statement);
                            break;
                        case "loop":
                            // TODO Deal with Loop Statement
                            // dealWithLoop(statement);
                            break;
                        case "do":
                            dealWithConditional(statement);
                            break;
                        case "console":
                            dealWithPrint(statement);
                            break;
                    }
                } else {
                    System.out.println("Not a valid statement: " + statement);
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            System.out.println("Error Finding File Path.");
            e.printStackTrace();
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
        Pattern prefix = Pattern.compile("loop <([A-Z]+|[0|1]+)->([A-Z]+|[0|1]+), ?[0|1]+> ?{.*}$"); // [A-Z]+->[0|1]+, ?[0|1]+
        Matcher matcher = prefix.matcher(statement);
        System.out.println(statement);
        System.out.println("find " + matcher.find());
    }

    static void dealWithPrint(String statement) {
        Pattern  pattern = Pattern.compile("console <.*>$");
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
                    System.out.print(printStatement[1].substring(1, printStatement[1].length()-1)); // Print literal stripped of surrounding ''
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
        Pattern prefix = Pattern.compile("do <.*> if <.*> otherwise do <.*>$"); // [A-Z]+->[0|1]+, ?[0|1]+
        Matcher matcher = prefix.matcher(statement);
        if (matcher.matches()) {
            String[] components = statement.split(" <|> |>$");
            String result;
            if ((result = parseCondition(components[3])).equals("true")) {
                System.out.println("returned true");
            } else if (result.equals("false")) {
                System.out.println("returned false");
            } else {
                System.out.println("Error during condition parse: " + components[3]);
                System.exit(1);
            }
        } else {
            System.out.println("Conditional must be done in the form do <.*> if <.*> otherwise do <.*>: " + statement);
        }
    }

    static String parseCondition(String condition) {
        Pattern pattern = Pattern.compile("(.*?)(not)(.+)|(.+?)(and)(.+)|(.+?)(or)(.+)|(.+?)(eq)(.+)|(.+?)(!eq)(.+)|(.+?)(gt)(.+)|(.+?)(gt=)(.+)|(.+?)(lt)(.+)|(.+?)(lt=)(.+)|(true)|(false)");
        Pattern element = Pattern.compile("([A-Z]+)|([0|1]+)");
        Matcher matcher = pattern.matcher(condition.trim());
        if (matcher.matches()) {
            if (matcher.group(2) != null) {
                String rightSide = parseCondition(matcher.group(3));
                if (rightSide.equals("error")) {
                    return rightSide;
                }
                if (rightSide.equals("true")) {
                    return "false";
                } else {
                    return "true";
                }
            } else if (matcher.group(5) != null) {
                String rightSide = parseCondition(matcher.group(6));
                String leftSide = parseCondition(matcher.group(4));
                if (rightSide.equals("error") || leftSide.equals("error")) {
                    return "error";
                }
                if (leftSide.equals("false") || rightSide.equals("false")) {
                    return "false";
                } else {
                    return "true";
                }
            } else if (matcher.group(8) != null) {
                String rightSide = parseCondition(matcher.group(9));
                String leftSide = parseCondition(matcher.group(7));
                if (rightSide.equals("error") || leftSide.equals("error")) {
                    return "error";
                }
                if (leftSide.equals("true") || rightSide.equals("true")) {
                    return "true";
                } else {
                    return "false";
                }
            } else if (matcher.group(11) != null) {
                System.out.println(matcher.group(10) + matcher.group(12));
                Matcher left = element.matcher(matcher.group(10));
                Matcher right = element.matcher(matcher.group(12));
                if (!left.matches() || !right.matches()) {
                    return "error";
                }
            } else if (matcher.group(14) != null) {
                return evaluateExpression(matcher.group(13), matcher.group(14), matcher.group(15));
            } else if (matcher.group(17) != null) {
                // TODO
                System.out.println(matcher.group(17));
                System.out.println(condition);
            } else if (matcher.group(20) != null) {
                // TODO
                System.out.println(matcher.group(20));
                System.out.println(condition);
            } else if (matcher.group(23) != null) {
                // TODO
                System.out.println(matcher.group(23));
                System.out.println(condition);
            } else if (matcher.group(26) != null) {
                // TODO
                System.out.println(matcher.group(26));
                System.out.println(condition);
            } else if (matcher.group(28) != null) {
                return "true";
            } else if (matcher.group(29) != null) {
                return "false";
            }
        }
        return "error";
    }

    static String evaluateExpression(String left, String operand, String right) {
        Integer leftValue = getValue(left);
        Integer rightValue = getValue(right);
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
                return Integer.parseInt(matcher.group(2));
            }
        }
        return null;
    }
}
