
// neccesarry imports for this file 
import java.io.*;
import java.lang.reflect.Executable;
import java.util.*;
import java.nio.file.Files;

class Parser {
    String commandName;
    String[] args;

    public boolean parse(String input) {
        // Split the input string into command name and args
        String[] parts = input.split(" ");
        commandName = parts[0];
        int start = 1;
        if (parts.length > 1 && parts[1].startsWith("-")) {
            commandName += " -r";
            ++start;

        }
        args = new String[parts.length - start];
        for (int i = 0; start < parts.length; start++, ++i) {
            args[i] = parts[start];
        }
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}

public class Terminal {
    Parser parser;

    public Terminal() {
        parser = new Parser();
    }

    // ... Implement other commands

    public void chooseCommandAction() throws IOException {
        String input = System.console().readLine("> ");
        // Parse the input
        boolean parsed = parser.parse(input);
        if (!parsed) {
            return;
        }

        // Get the command name and args
        String commandName = parser.getCommandName();
        String[] args = parser.getArgs();

        // Call the appropriate command method
        switch (commandName) {
            case "echo":
                echo(args);
                break;

            case "pwd":
                System.out.println(pwd());
                break;

            case "cd":
                cd(args);
                break;

            case "ls -r":
                lsReverse();
                break;

            case "ls":
                ls();
                break;

            case "mkdir":
                mkdir(args);
                break;

            case "rmdir":
                rmdir(args);
                break;

            case "touch":

                touch(args);
                break;

            case "cp":
                cp(args);
                break;

            case "cp -r":
                cpRecursive(args);
                break;

            case "rm":
                rm(args);
                break;

            case "cat":
                cat(args);
                break;

            case "wc":
                wc(args);
                break;

            case "exit":
                System.exit(0);
                break;

            default:
                System.out.println("Unknown command: " + commandName);
        }
    }

    // Implement each command in a method
    public String pwd() {
        return System.getProperty("user.dir");
    }

    public void cd(String[] args) throws IOException {
        if (args.length == 0) {
            // Change to home directory
            System.setProperty("user.dir", System.getProperty("user.home"));
        } else if (args[0].equals("..")) {
            // Change to parent directory
            String currentDir = System.getProperty("user.dir");
            String parentDir = new File(currentDir).getParent();
            System.setProperty("user.dir", parentDir);
        } else {
            // Change to the specified directory
            System.setProperty("user.dir", System.getProperty("user.dir") + "/" + args[0]);
        }
    }

    // echo
    public void echo(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            System.out.print(args[i] + ' ');
        }
        System.out.println();
    }

    // ls
    public void ls() throws IOException {
        File currentDir = new File(System.getProperty("user.dir"));
        File[] files = currentDir.listFiles();

        // Sort the files alphabetically
        Arrays.sort(files, Comparator.comparing(File::getName));

        // Print the file names
        for (File file : files) {
            System.out.println(file.getName());
        }
    }

    public void lsReverse() throws IOException {
        File currentDir = new File(System.getProperty("user.dir"));
        File[] files = currentDir.listFiles();

        // Sort the files alphabetically
        Arrays.sort(files, Comparator.comparing(File::getName).reversed());

        // Print the file names
        for (File file : files) {
            System.out.println(file.getName());

        }
    }

    public void mkdir(String[] args) throws IOException {
        for (String arg : args) {
            File newDir = new File(arg);
            if (newDir.mkdir()) {
                System.out.println("Directory created successfully");
            } else {
                System.out.println("Failed to create directory");
            }
        }
    }

    public void rmdir(String[] args) throws IOException {
        for (String arg : args) {
            File dirToDelete = new File(arg);
            if (!dirToDelete.exists()) {
                System.out.println("Directory does not exist: " + arg);
            } else if (dirToDelete.listFiles().length > 0) {
                System.out.println("Directory is not empty: " + arg);
            } else {
                dirToDelete.delete();
            }
        }
    }

    public void touch(String[] args) throws IOException {
        for (String arg : args) {
            File newFile = new File(arg);
            if (!newFile.exists()) {
                newFile.createNewFile();
            } else {
                System.out.println("File already exists: " + arg);
            }
        }
    }

    public void cp(String[] args) throws IOException {
        File sourceFile = new File(args[0]);
        File targetFile = new File(args[1]);

        if (!sourceFile.exists()) {
            System.out.println("Source file does not exist: " + args[0]);
        } else if (!targetFile.getParentFile().exists()) {
            System.out.println("Target directory does not exist: " + targetFile.getParentFile().getPath());
        } else {
            Files.copy(sourceFile.toPath(), targetFile.toPath());
        }
    }

    // cp -r

    public void cpRecursive(String[] args) throws IOException {
        File sourceFile = new File(args[0]);
        File targetFile = new File(args[1]);

        if (!sourceFile.exists()) {
            System.out.println("Source file does not exist: " + args[0]);
        } else if (!targetFile.getParentFile().exists()) {
            System.out.println("Target directory does not exist: " + targetFile.getParentFile().getPath());
        } else {
            Files.walk(sourceFile.toPath()).forEach(source -> {
                try {
                    Files.copy(source, targetFile.toPath().resolve(sourceFile.toPath().relativize(source)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void rm(String[] args) throws IOException {
        for (String arg : args) {
            File fileToDelete = new File(arg);
            if (!fileToDelete.exists()) {
                System.out.println("File does not exist: " + arg);
            } else if (fileToDelete.isDirectory()) {
                System.out.println("Cannot delete directory: " + arg);
            } else {
                fileToDelete.delete();
            }
        }
    }

    public void cat(String[] args) throws IOException {
        if (args.length == 1) {
            // Print the contents of the specified file
            File file = new File(args[0]);
            if (!file.exists()) {
                System.out.println("File does not exist: " + args[0]);
            } else {
                Files.lines(file.toPath()).forEach(System.out::println);
            }
        } else if (args.length == 2) {
            // Concatenate the contents of the two specified files and print the result
            File file1 = new File(args[0]);
            File file2 = new File(args[1]);

            if (!file1.exists()) {
                System.out.println("File does not exist: " + args[0]);
                return;
            } else if (!file2.exists()) {
                System.out.println("File does not exist: " + args[1]);
                return;
            }

            String output = Files.readString(file1.toPath()) + Files.readString(file2.toPath());
            System.out.println(output);
        } else {
            System.out.println("Invalid usage: cat [file] [file]");
        }
    }

    // wc (word count)
    public void wc(String[] args) throws IOException {
        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("File does not exist: " + args[0]);
        } else {
            int lines = 0;
            int words = 0;
            int characters = 0;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                lines++;
                words += line.split(" ").length;
                characters += line.length();
            }
            reader.close();

            System.out.println("Lines: " + lines);
            System.out.println("Words: " + words);
            System.out.println("Characters: " + characters);
        }
    }

    public static void main(String[] args) throws IOException {

        Terminal terminal = new Terminal();

        // Start the CLI loop
        while (true) {
            terminal.chooseCommandAction();

            // Exit the CLI if the user enters the "exit" command
            if (terminal.parser.getCommandName().equals("exit")) {
                break;
            }
        }
    }
}