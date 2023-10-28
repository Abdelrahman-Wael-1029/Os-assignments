import java.io.*;
import java.util.*;
import java.nio.file.*;

class Parser {
    String commandName;
    String[] args;

    public boolean parse(String input) {
        // Split the input string into command name and args
        String[] parts = input.split(" ");
        commandName = parts[0];
        args = new String[parts.length - 1];
        int start = 1;
        if (parts.length == 1) {
            args = new String[0];
            return true;
        }
        if (parts[1].equals("-r")) {
            commandName += " -r";
            ++start;
        }
        for (int i = start; i < parts.length; ++i) {
            if (parts[i].startsWith("\"")) {
                // Combine the parts of the argument into a single string
                StringBuilder arg = new StringBuilder(parts[i++]);
                while (i < parts.length && !parts[i].endsWith("\"")) {
                    arg.append(" ").append(parts[i]);
                    ++start;
                    ++i;
                }
                if (i != parts.length) {
                    arg.append(" ").append(parts[i]);
                }
                args[i - ++start] = arg.toString();
                continue;
            }
            args[i - start] = parts[i];
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
    String[] commandHistory = new String[100];

    public Terminal() {
        parser = new Parser();
    }

    // ... Implement other commands

    public void chooseCommandAction() throws IOException {
        System.out.print(pwd() + " > ");
        String input = new Scanner(System.in).nextLine();
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
            case "echo" -> echo(args);
            case "pwd" -> System.out.println(pwd());
            case "cd" -> cd(args);
            case "ls -r" -> lsReverse();
            case "ls" -> ls();
            case "mkdir" -> mkdir(args);
            case "rmdir" -> rmdir(args);
            case "touch" -> touch(args);
            case "cp" -> cp(args);
            case "cp -r" -> cpRecursive(args);
            case "rm" -> rm(args);
            case "cat" -> cat(args);
            case "wc" -> wc(args);
            case "history" -> history();
            case "exit" -> System.exit(0);
            default -> System.out.println("Unknown command: " + commandName);
        }
    }


    // Implement each command in a method
    public void echo(String[] args) {
        for (String arg : args) {
            System.out.print(arg + ' ');
        }
        System.out.println();
    }

    public String pwd() {
        return System.getProperty("user.dir");
    }

    public void cd(String[] args) {
        if (args.length == 0) {
            // Change to home directory
            System.setProperty("user.dir", System.getProperty("user.home"));
        } else if (args[0].equals("..")) {
            // Change to parent directory
            File currentDir = new File(System.getProperty("user.dir"));
            System.setProperty("user.dir", currentDir.getParent());
        } else if (!args[0].equals(".")) {
            // Change to the specified directory using paths
            if (args[0].startsWith("\"") && args[0].endsWith("\"")) {
                args[0] = args[0].substring(1, args[0].length() - 1);
            }
            Path path = Paths.get(args[0]);
            if (!path.isAbsolute()) {
                path = Paths.get(System.getProperty("user.dir"), args[0]);
            }
            if (Files.exists(path)) {
                System.setProperty("user.dir", path.toString());
            } else {
                System.out.println("Directory does not exist: " + args[0]);
            }
        }
    }

    public void ls() {
        File currentDir = new File(System.getProperty("user.dir"));
        File[] files = currentDir.listFiles();

        // Sort the files alphabetically
        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));

            // Print the file names
            for (File file : files) {
                System.out.println(file.getName());
            }
        }
    }

    public void lsReverse() {
        File currentDir = new File(System.getProperty("user.dir"));
        File[] files = currentDir.listFiles();

        // Sort the files alphabetically
        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName).reversed());

            // Print the file names
            for (File file : files) {
                System.out.println(file.getName());
            }
        }
    }

    public void mkdir(String[] args) {
        for (String arg : args) {
            File newDir = new File(arg);
            if (newDir.mkdir()) {
                System.out.println("Directory created successfully");
            } else {
                System.out.println("Failed to create directory");
            }
        }
    }

    public void rmdir(String[] args) {
        for (String arg : args) {
            File dirToDelete = new File(arg);
            if (!dirToDelete.exists()) {
                System.out.println("Directory does not exist: " + arg);
            } else if (Objects.requireNonNull(dirToDelete.listFiles()).length > 0) {
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
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void rm(String[] args) {
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
            System.out.println(args[0]);
        }

    }

    public void history() {
        for (String s : commandHistory) {
            if (s == null) {
                break;
            }
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws IOException {

        Terminal terminal = new Terminal();

        // Start the CLI loop
        while (true) {
            try {
                terminal.chooseCommandAction();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
