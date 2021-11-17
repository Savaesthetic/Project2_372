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
                    // TODO IS THIS ENOUGH TO CONSTITUE A COMPILATION ERROR?
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
                // TODO COMPILATION ERROR
                System.out.println("Variables can only be assigned to binary values: " + statement);
            }
        } else {
            // TODO COMPILATION ERROR
            System.out.println("Variable names can only include [A-Z]: " + statement);
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
                    // TODO COMPILATION ERROR TOO BROAD?
                    System.out.println("Can only output one variable, value, or string literal at a time: " + statement);
                }
            }
        } else {
            // TODO DEAL WITH COMPILATION ERROR
            System.out.println("Must use console <> to print to console: " + statement);
        }
    }

    static void dealWithConditional(String statement) {
        // TODO CREATE CONDITION CHECKER
        Pattern prefix = Pattern.compile("do <.*> if <.*> otherwise do <.*>$"); // [A-Z]+->[0|1]+, ?[0|1]+
        Matcher matcher = prefix.matcher(statement);
        if (matcher.find()) {
            String[] components = statement.split(" ");
            String condition = components[3].substring(1, components[3].length());
            // Figure out how to parse down the condition
        } else {
            System.out.println("Conditional must be done in the form do <.*> if <.*> otherwise do <.*>: " + statement);
        }
    }
}
