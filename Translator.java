import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {
    static HashMap<String, Integer> variables = new HashMap<String, Integer>(); // Holds all variables for the current program
    static int depth = 0; // Holds the depth of the current execution block
    static String[] delimiters = new String[]{"\\?", "\\!", "\\@", "\\#", "\\$", "\\%"}; // Holds delimiters for each depth, only allows for depth up to 5
    static String[] commandLineArgs = new String[]{null, "CLONE", "CLTWO", "CLTHREE", "CLFOUR", "CLFIVE"}; // Holds the names for up to five command line arguments

    /* main sets all of the command line arguments and opens and stores the 
    program in my programming langauge into a string */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Error, need to at least pass in file to interpret.");
            System.exit(0);
        }
        for (int i = 1; i < args.length; i++) {
            variables.put(commandLineArgs[i], Integer.parseInt(args[i]));
        }
        try {
            String program = Files.readString(Path.of(args[0]));
            parseStatements(program, "\\?");
        } catch (IOException e) {
            System.out.println("Error Finding File Path. File must be in same directory as java file.");
        }
    }

    // parseStatements parses the given statementBlock by teh givin delimiter
    static void parseStatements(String statementBlock, String delimiter) {
        String[] statements = statementBlock.split(delimiter);

        for (String statement: statements) {
            statement = statement.trim();
            Pattern prefix = Pattern.compile("var|loop|do|console|exit");
            Matcher matcher = prefix.matcher(statement);

            // evaluates each statement by the keyword prefacing it
            if (matcher.find()) {
                switch (matcher.group()) {
                    case "var":
                        variableAssignment(statement);
                        break;
                    case "console":
                        printStatements(statement);
                        break;
                    case "do":
                        runConditional(statement);
                        break;
                    case "loop":
                        runLoop(statement);
                        break;
                    case "exit":
                        handleExit(statement);
                        break;
                }
            } else {
                System.out.println("Not a valid statement: " + statement);
                System.exit(1);
            }
        }
        depth--;
    }

    /* variable Assignment takes in a statement and assigns sets a variable in the
    variable hashtable if valid or throws an error */
    static void variableAssignment(String statement) {
        Pattern pattern = Pattern.compile("var [A-Z]+:.*");
        Matcher matcher = pattern.matcher(statement);
        if (matcher.matches()) {
            pattern = Pattern.compile("([0|1]+)|([A-Z]+)|(math<(add|sub|mul|div|mod), ([0|1]+|[A-Z]+), ([0|1]+|[A-Z]+)>)");
            matcher = pattern.matcher(statement.split(":")[1]);
            if (matcher.matches()) {
                String[] var = statement.split(" |:"); // name = var[1], val = var[2]
                if (matcher.group(1) != null) {
                    variables.put(var[1], Integer.parseInt(var[2], 2)); // Convert from binary to decimal and add to dictionary
                } else if (matcher.group(2) != null) {
                    variables.put(var[1], getVariableIntegerValue(var[2]));
                } else {
                    variables.put(var[1], getMathIntegerValue(matcher.group(4), matcher.group(5), matcher.group(6)));
                }
            } else {
                System.out.println("Variables can only be assigned to binary values, variable, or a single arithemetic expression: " + statement);
                System.exit(1);
            }
        } else {
            System.out.println("Variable assignment must begin with var NAME: where the name can only include [A-Z]+ characters: " + statement);
            System.exit(1);
        }
    }

    /* returns the decimal value of a given variable if it exists
    or throws an error if not */
    static Integer getVariableIntegerValue(String statement) {
        Integer value = variables.get(statement);
        if (value == null) {
            System.out.println("Variable does not exist: " + statement);
            System.exit(1);
        }
        return value;
    }

    /* calculates the decimal value of the given arithmetic expression and
    returns the result if valid */
    static Integer getMathIntegerValue(String op, String left, String right) {
        switch(op) {
            case "add":
                return getIntegerValue(left) + getIntegerValue(right);
            case "sub":
                return getIntegerValue(left) - getIntegerValue(right);
            case "mul":
                return getIntegerValue(left) * getIntegerValue(right);
            case "div":
                return getIntegerValue(left) / getIntegerValue(right);
            case "mod":
                return getIntegerValue(left) % getIntegerValue(right);
        }
        System.out.println("Error with arithmetic math<" + op + ", " + left + ", " + right + ">");
        System.exit(1);
        return null;
    }

    /* returns the decimal value of a given variable or binary string */
    static Integer getIntegerValue(String statement) {
        Integer val;
        if ((val = variables.get(statement)) != null) {
            return val;
        }
        Pattern pattern = Pattern.compile("[0|1]+");
        Matcher matcher = pattern.matcher(statement);
        if (matcher.matches()) {
            return Integer.parseInt(statement, 2);
        }
        System.out.println("Error illegal value: " + statement);
        System.exit(1);
        return null;
    }

    /* handles the interpretation of the printing statement for my language */
    static void printStatements(String statement){
        Pattern  pattern = Pattern.compile("console <.*>");
        Matcher matcher = pattern.matcher(statement);
        if (matcher.matches()) {
            CharSequence component = statement.subSequence(9, statement.length()-1);
            if (component.length() == 0) {
                System.out.println();
                return;
            }
            pattern = Pattern.compile("([1|0]+)|('.*')|([A-Z]+)");
            matcher = pattern.matcher(component);

            if (matcher.matches()) {
                if (matcher.group(1) != null) {
                    System.out.println(Integer.parseInt(component.toString(), 2));
                } else if (matcher.group(2) != null) {
                    System.out.println(component.subSequence(1, component.length()-1));
                } else {
                    System.out.println(getVariableIntegerValue(component.toString()));
                }
            } else {
                System.out.println("Can only output one variable, value, or string literal at a time: " + statement);
                    System.exit(1);
            }
        } else {
            System.out.println("Must use console <> to print to console: " + statement);
            System.exit(1);
        }
    }

    /* handles the conditional interpretation for my programming language */
    static void runConditional(String statement) {
        Pattern prefix = Pattern.compile("do <([\\s\\S]*?)> if <(.*)> otherwise do <([\\s\\S]*)>");
        Matcher matcher = prefix.matcher(statement);
        if (matcher.matches()) {
            if (evaluateCondition(matcher.group(2)).equals("true")) {
                if (matcher.group(1).length() == 0) {
                    return;
                }
                depth++;
                parseStatements(matcher.group(1), getDelimiter());
            } else {
                if (matcher.group(3).length() == 0) {
                    return;
                }
                depth++;
                parseStatements(matcher.group(3), getDelimiter());
            }
        } else {
            System.out.println("Error parsing conditional, must be in form: do <> if <> otherwise do <> but was given: " + statement);
            System.exit(1);
        }
    }

    static String evaluateCondition(String condition) {
        Pattern pattern = Pattern.compile("(.*?)( not )(.+)|(.+?)( and )(.+)|(.+?)( or )(.+)|(.+?)( !eq )(.+)|(.+?)( eq )(.+)|(.+?)( gt )(.+)|(.+?)( gt= )(.+)|(.+?)( lt )(.+)|(.+?)( lt= )(.+)|(true)|(false)");
        Matcher matcher = pattern.matcher(condition.trim());
        if (matcher.matches()) {
            if (matcher.group(2) != null) {
                String rightSide = evaluateCondition(matcher.group(3).trim());
                if (rightSide.equals("true")) {
                    return "false";
                } else {
                    return "true";
                }
            } else if (matcher.group(5) != null) {
                String rightSide = evaluateCondition(matcher.group(6).trim());
                String leftSide = evaluateCondition(matcher.group(4).trim());
                if (leftSide.equals("false") || rightSide.equals("false")) {
                    return "false";
                } else {
                    return "true";
                }
            } else if (matcher.group(8) != null) {
                String rightSide = evaluateCondition(matcher.group(9).trim());
                String leftSide = evaluateCondition(matcher.group(7).trim());
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
        System.out.println("Error evaluating condition: " + condition);
        System.exit(1);
        return "Error";
    }

    static String evaluateExpression(String left, String operand, String right) {
        Integer leftValue;
        Integer rightValue;
        Pattern pattern = Pattern.compile("math<(add|sub|mul|div|mod), ([0|1]+|[A-Z]+), ([A-Z]+|[0|1]+)>$");
        Matcher leftMatcher = pattern.matcher(left);
        Matcher rightMatcher = pattern.matcher(right);
        if (leftMatcher.matches()) {
            leftValue = getMathIntegerValue(leftMatcher.group(1), leftMatcher.group(2), leftMatcher.group(3));
        } else {
            leftValue = getIntegerValue(left);
        }
        if (rightMatcher.matches()) {
            rightValue = getMathIntegerValue(rightMatcher.group(1), rightMatcher.group(2), rightMatcher.group(3));
        } else {
            rightValue = getIntegerValue(right);
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

    /* checks to make sure the loop statement is declared correctly */
    static void runLoop(String statement) {
        Pattern prefix = Pattern.compile("loop <([A-Z]+|[0|1]+)->([A-Z]+|[0|1]+), (math<(add|sub|mul|div|mod), (op|[A-Z]+), ([A-Z]+|[0|1]+)>)> \\{([\\s\\S]+)\\}"); // \\{.*\\}$
        Matcher matcher = prefix.matcher(statement);
        if (matcher.matches()) {
            loopHelper(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(5), matcher.group(6), matcher.group(7));
        } else {
            System.out.println("Error with loop declaration: " + statement);
            System.exit(1);
        }
    }

    /* handles the loop interpretation for my langauge */
    static void loopHelper(String startVal, String endVal, String operator, String mathLeft, String mathRight, String statementBlock) {
        Integer left;
        Integer right;
        if (mathLeft.equals("op")) {
            left = Integer.parseInt(startVal, 2);
            right = getIntegerValue(endVal);
            if (operator.equals("add") || operator.equals("mul")) {
                while (left <= right) {
                    depth++;
                    parseStatements(statementBlock, getDelimiter());
                    left = getMathIntegerValue(operator, Integer.toBinaryString(left), mathRight);
                }
            } else {
                while (left >= right) {
                    depth++;
                    parseStatements(statementBlock, getDelimiter());
                    left = getMathIntegerValue(operator, Integer.toBinaryString(left), mathRight);
                }
            }
        } else {
            left = getVariableIntegerValue(startVal);
            right = getIntegerValue(endVal);
            if (operator.equals("add") || operator.equals("mul")) {
                while (left <= right) {
                    depth++;
                    parseStatements(statementBlock, getDelimiter());
                    left = getMathIntegerValue(operator, mathLeft, mathRight);
                    if (startVal.equals(mathLeft)) {
                        variables.put(startVal, left);
                    }
                }
            } else {
                while (left >= right) {
                    depth++;
                    parseStatements(statementBlock, getDelimiter());
                    left = getMathIntegerValue(operator, mathLeft, mathRight);
                    if (startVal.equals(mathLeft)) {
                        variables.put(startVal, left);
                    }
                }
            }
        }
    }

    /* handles the exit interpretation for my language, simply terminates
    the running interpretation */
    static void handleExit(String statement) {
        Pattern pattern = Pattern.compile("exit");
        Matcher matcher = pattern.matcher(statement);
        if (matcher.matches()) {
            System.exit(1);
        } else {
            System.out.println("Error with exit statement: " + statement);
            System.exit(1);
        }
    }

    /* returns the delimiter used for parsing for the current depth */
    static String getDelimiter() {
        return delimiters[depth];
    }
}