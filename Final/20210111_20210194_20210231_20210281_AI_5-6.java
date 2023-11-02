import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.Stream;

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
                StringBuilder arg = new StringBuilder(parts[i]);
                while (i + 1 < parts.length && !parts[i + 1].endsWith("\"")) {
                    arg.append(" ").append(parts[i + 1]);
                    ++start;
                    ++i;
                }
                if (i + 1 != parts.length) {
                    arg.append(" ").append(parts[i++ + 1]);
                    ++start;
                }
                args[i - start] = arg.toString();
                continue;
            }
            args[i - start] = parts[i];
        }
        args = Arrays.copyOf(args, parts.length - start);
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}

class Terminal {
    Parser parser;
    ArrayList<String> commandHistory = new ArrayList<>();

    public Terminal() {
        parser = new Parser();
    }


    public void chooseCommandAction() throws IOException {
        System.out.print("> ");
        String input = new Scanner(System.in).nextLine();

        // Parse the input
        boolean parsed = parser.parse(input);
        if (!parsed) {
            return;
        }

        // Get the command name and args
        String commandName = parser.getCommandName();
        String[] args = parser.getArgs();

        // Add the command to the history
        commandHistory.add(commandName);

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
        } else {
            // remove quotes if present
            if (args[0].length() > 1 && args[0].startsWith("\"") && args[0].endsWith("\"")) {
                args[0] = args[0].substring(1, args[0].length() - 1);
            }
            Path path = Paths.get(args[0]).normalize();
            if (!path.isAbsolute()) {
                path = Paths.get(System.getProperty("user.dir"), args[0]).normalize();
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
                System.out.print(file.getName() + "    ");
            }
            System.out.println();
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
                System.out.print(file.getName() + "    ");
            }
            System.out.println();
        }
    }

    public void mkdir(String[] args) {
        for (String arg : args) {
            if (arg.length() > 1 && arg.startsWith("\"") && arg.endsWith("\"")) {
                arg = arg.substring(1, arg.length() - 1);
            }
            File newDir = new File(arg);
            if (!newDir.mkdir()) {
                System.out.println("Directory name is invalid: " + arg);
            }
        }
    }

    public void rmdir(String[] args) {
        for (String arg : args) {
            if (arg.length() > 1 && arg.startsWith("\"") && arg.endsWith("\"")) {
                arg = arg.substring(1, arg.length() - 1);
            }
            File dirToDelete = new File(arg);
            if (!dirToDelete.exists() || !dirToDelete.isDirectory()) {
                System.out.println("Directory does not exist: " + arg);
            } else if (!dirToDelete.delete()) {
                System.out.println("Failed to delete directory (not empty?): " + arg);
            }
        }
    }

    public void touch(String[] args) throws IOException {
        for (String arg : args) {
            if (arg.length() > 1 && arg.startsWith("\"") && arg.endsWith("\"")) {
                arg = arg.substring(1, arg.length() - 1);
            }
            File newFile = new File(arg);
            if (!newFile.exists()) {
                if (!newFile.createNewFile()) {
                    System.out.println("Failed to create file: " + arg);
                }
            } else {
                System.out.println("File already exists: " + arg);
            }
        }
    }

    public void cp(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Invalid usage: cp [source] [target]");
            return;
        }
        if (args[0].length() > 1 && args[0].startsWith("\"") && args[0].endsWith("\"")) {
            args[0] = args[0].substring(1, args[0].length() - 1);
        }
        if (args[1].length() > 1 && args[1].startsWith("\"") && args[1].endsWith("\"")) {
            args[1] = args[1].substring(1, args[1].length() - 1);
        }
        File sourceFile = new File(args[0]);
        File targetFile = new File(args[1]);
        // Takes 2 arguments, both are files and copies the first onto the second.
        if (!sourceFile.exists()) {
            System.out.println("Source file does not exist: " + args[0]);
        } else if (!targetFile.exists() && !targetFile.createNewFile()) {
            System.out.println("Failed to create file: " + args[1]);
        } else {
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void cpRecursive(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Invalid usage: cp -r [source] [target]");
            return;
        }
        if (args[0].length() > 1 && args[0].startsWith("\"") && args[0].endsWith("\"")) {
            args[0] = args[0].substring(1, args[0].length() - 1);
        }
        if (args[1].length() > 1 && args[1].startsWith("\"") && args[1].endsWith("\"")) {
            args[1] = args[1].substring(1, args[1].length() - 1);
        }
        // Takes 2 arguments, both are directories (empty or not) and copies the first
        // directory (with all its content) into the second one.
        File sourceDir = new File(args[0]);
        File targetDir = new File(args[1]);
        if (!sourceDir.exists()) {
            System.out.println("Source directory does not exist: " + args[0]);
            return;
        } else if (!targetDir.exists()) {
            if (!targetDir.mkdir()) {
                System.out.println("Failed to create directory: " + args[1]);
            }
        } else {
            // make a folder in targetDir with the same name as sourceDir
            File newDir = new File(targetDir, sourceDir.getCanonicalFile().getName());
            if (!newDir.mkdir()) {
                System.out.println("Failed to create directory: " + newDir.getName());
            }
            targetDir = newDir;
        }
        Stream<Path> fileStream = Files.walk(sourceDir.toPath());
        File finalTargetDir = targetDir;
        fileStream.forEach(source -> {
            Path target = finalTargetDir.toPath().resolve(sourceDir.toPath().relativize(source));
            try {
                // source to target if not empty
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Failed to copy " + source + " to " + target + ": " + e.getMessage());
            }
        });
        fileStream.close();
        /*
         * Files.walk(sourceDir.toPath()) returns a Stream of all files and directories
         * in the sourceDir directory and its subdirectories.
         * forEach(source -> {...}) applies the given function to each element of the
         * stream, which in this case is a lambda expression that takes a Path object
         * called source.
         * Path target =
         * targetDir.toPath().resolve(sourceDir.toPath().relativize(source)); creates a
         * Path object called target that represents the corresponding path in the
         * targetDir directory
         * for the source path. This is done by first getting the relative path of
         * source with respect to sourceDir, and then resolving it against targetDir.
         * Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING); copies the
         * file or directory represented by source to the corresponding path represented
         * by target, with the option to replace the target file if it already exists.
         * If an IOException occurs during the copy operation, it is caught and the
         * stack trace is printed to the console using e.printStackTrace().
         */
    }

    public void rm(String[] args) {
        for (String arg : args) {
            File fileToDelete = new File(arg);
            if (!fileToDelete.exists() || !fileToDelete.isFile()) {
                System.out.println("File does not exist: " + arg);
            } else {
                if (!fileToDelete.delete()) {
                    System.out.println("Failed to delete file (in use?): " + arg);
                }
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
                Stream<String> fileStream = Files.lines(file.toPath());
                fileStream.forEach(System.out::println);
                fileStream.close();
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
        if (args.length != 1) {
            System.out.println("Invalid usage: wc [file]");
            return;
        }
        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("File does not exist: " + args[0]);
        } else {
            int lines = 0, words = 0, characters = 0;

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
        for (int i = 0; i < commandHistory.size(); ++i) {
            System.out.println(i + 1 + ". " + commandHistory.get(i));
        }
    }

    public static void main(String[] args) {

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
