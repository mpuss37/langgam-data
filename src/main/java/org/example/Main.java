package org.example;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main {
    private static String filePath, nameFile, nameFileTree, base64Data;
    private static ConvertFile convertFile = new ConvertFile();

    private static void menuMain(String[] args) throws Exception {
        if (args.length == 3 && args[0].equals("-n") || args[0].equals("--normal")) {
            filePath = String.valueOf(args[1]);
            nameFile = String.valueOf(args[2]);
            base64Data = convertFile.encodeFile(filePath, nameFile);
        } else if (args.length == 4 && args[0].equals("-e") || args[0].equals("--extract")) {
            filePath = String.valueOf(args[1]);
            nameFileTree = String.valueOf(args[2]);
            nameFile = String.valueOf(args[3]);
            convertFile.decodeFile(filePath, nameFileTree, nameFile);
        } else if (args[0].equals("-h") || args[0].equals("--help")) {
            System.out.println("Langgam-Data (version 1.0)");
            System.out.println("Usage:\n" +
                    " langgam [OPTIONS]...[VALUES]\t\n" +
                    "  -n, --normal [target] [name] [hash]   compress your file.\n" +
                    "  -e, --extract [target] [key] [name]   extract file.\n" +
                    "  -h, --help          Display usage,options and help.\n");
        } else {
            System.out.println("langgam-data: missing operand\n" +
                    "Try 'langgam-data -h or --help' for more information.");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("langgam: missing operand\n" +
                    "Try 'langgam -h or --help' for more information.");
        } else {
            menuMain(args);
        }
    }
}