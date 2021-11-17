import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Project {
    static HashMap<String, Integer> variables = new HashMap<String, Integer>();
    public static void main(String[] args){
        try {
            String program = Files.readString(Path.of("assignment.txt"));
            String[] statements = program.split("\\?");
            for (String statement: statements){
                Pattern prefix = Pattern.compile("var|loop|if|console");
                Matcher matcher = prefix.matcher(statement);
                // System.out.println(" beginning ");
                // System.out.println("tostring " + matcher.toString());
                // System.out.println("pattern " + matcher.pattern());
                // System.out.println("matches " + matcher.matches());
                // System.out.println("find " + matcher.find());
                // System.out.println("group2 " + matcher.group());
                if (matcher.find()) {
                    // todo deal with found statement
                    switch (matcher.group()) {
                        case "var":
                            dealWithVar(statement);
                            break;
                        case "loop":
                            // dealWithLoop(statement);
                            break;
                        case "if":
                            System.out.println("found loop");
                            break;
                        case "console":
                            dealWithPrint(statement);
                            break;
                    }
                } else {
                    System.out.println("Not a valid statement: " + statement);
                    // TODO compilation error
                }
            }
        } catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    static void dealWithVar(String statement) 
    {
        Pattern pattern = Pattern.compile("var [A-Z]+:"); // "var [A-Z]+:[0|1]+$"
        Matcher matcher = pattern.matcher(statement);
        if (matcher.find()) {
            pattern = Pattern.compile("^[0|1]+$");
            matcher = pattern.matcher(statement.split(":")[1]);
            if (matcher.find()) {
                String[] var = statement.split(" |:"); // name = var[1], val = var[2]
                // TODO PARSE BINARY INTO DECIMAL AND ADD TO HASHMAP
                Integer value = Integer.parseInt(var[2], 2); // convert from binary string to integer
                variables.put(var[1], value);
                System.out.println("Valid statement: " + statement);
            } else {
                // TODO COMPILATION ERROR
                System.out.println("Variables can only be assigned to binary values: " + statement);
            }
        } else {
            // TODO COMPILATION ERROR
            System.out.println("Variable names can only include [A-Z]: " + statement);
        }
    }

    // static void dealWithLoop(String statement)
    // {
    //     Pattern prefix = Pattern.compile("loop <[A-Z]+->[0|1]+, ?[0|1]+> {(.*)}$");
    //     Matcher matcher = prefix.matcher(statement);
    //     System.out.println(statement);
    //     System.out.println("find " + matcher.find());
    // }

    static void dealWithPrint(String statement) 
    {
        //Pattern prefix = Pattern.compile("console <[A-Z]+|[1|0]+|'.*'>$"); // is there a way to tell hich or I'm matching with
        Pattern  pattern = Pattern.compile("console <.*>$");
        Matcher matcher = pattern.matcher(statement);
        // can split up into console <.*>$ and split strip < > and then do a regex for [A-Z]+|[1|0]+|'.*' for more in depth error checking
        if (matcher.matches()) {
            Pattern val = Pattern.compile("[1|0]?");
            Pattern literal = Pattern.compile("'.*'");
            Pattern var = Pattern.compile("[A-Z]+");
            matcher = pattern.matcher(statement.split("<|>")[1]);
            if (val.matcher(statement.split("<|>")[1]).matches()) { // might have to seperate into multiple ifs and patterns
                // TODO DEAL WITH VAL
                System.out.println("Valid print statement: " + statement);
            } else if (literal.matcher(statement.split("<|>")[1]).matches()){
                // TODO Deal with literal
            } else if (var.matcher(statement.split("<|>")[1]).matches()) {
                System.out.println("Can only output one variable, value, or string literal at a time: " + statement);
            } else {

            }
        } else {
            // TODO DEAL WITH COMPILATION ERROR
            System.out.println("Must use output <> to print to console: " + statement);
        }
    }

    static void dealWithConditional(String statement) {
        // TODO CREATE CONDITION REGEX CHECKER
    }
}
