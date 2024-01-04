package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ConvertFile {
    private File file;
    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;
    private byte[] fileData;
    private String base64Encoded, base64Decoded, fullPath, userDirectory = System.getProperty("user.dir");

    public static Node2 buildHuffmanTree(Map<Character, Integer> frekuensiKarakter) {
        PriorityQueue<Node2> minHeap = new PriorityQueue<>();

        // Membuat node untuk setiap karakter dan menambahkannya ke minHeap
        for (Map.Entry<Character, Integer> entry : frekuensiKarakter.entrySet()) {
            minHeap.offer(new Node2(entry.getKey(), entry.getValue(), null, null));
        }

        // Membuat pohon Huffman
        while (minHeap.size() > 1) {
            Node2 kiri = minHeap.poll();
            Node2 kanan = minHeap.poll();

            int totalFrekuensi = kiri.frekuensi + kanan.frekuensi;
            minHeap.offer(new Node2('\0', totalFrekuensi, kiri, kanan));
        }

        return minHeap.poll();
    }

    public static void generateCodes(Node2 root, String kode, Map<Character, String> tabelKode) {
        if (root == null) {
            return;
        }

        if (root.karakter != '\0') {
            tabelKode.put(root.karakter, kode);
        }

        generateCodes(root.kiri, kode + "0", tabelKode);
        generateCodes(root.kanan, kode + "1", tabelKode);
    }

    public String encodeHuffman(String kalimat, Map<Character, String> tabelKode) {
        StringBuilder encoded = new StringBuilder();

        for (char karakter : kalimat.toCharArray()) {
            encoded.append(tabelKode.get(karakter));
        }

        return encoded.toString();
    }

    public static String decodeHuffman(Node2 root, String encoded) {
        StringBuilder decoded = new StringBuilder();
        Node2 current = root;

        for (char bit : encoded.toCharArray()) {
            if (bit == '0') {
                current = current.kiri;
            } else {
                current = current.kanan;
            }

            if (current.karakter != '\0') {
                decoded.append(current.karakter);
                current = root;
            }
        }

        return decoded.toString();
    }

    public static String readFileContent(String filename) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            System.err.println("file not found !!!");
        }
        return content.toString();
    }

    public static void saveHuffmanTree(Node2 root, String filename) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            outputStream.writeObject(root);
            System.out.println("key path: " + filename);
        } catch (IOException e) {
            System.err.println("failed save key file");
        }
    }

    public static Node2 loadHuffmanTree(String filename) {
        Node2 root = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            root = (Node2) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return root;
    }

    String encodeFile(String filePath, String nameFile) {
        try {
            file = new File(filePath);
            fileInputStream = new FileInputStream(file);
            fileData = new byte[(int) file.length()];
            fileInputStream.read(fileData);
            fileInputStream.close();
            base64Encoded = Base64.getEncoder().encodeToString(fileData);

            Map<Character, Integer> frekuensiKarakter = new HashMap<>();
            for (char c : base64Encoded.toCharArray()) {
                frekuensiKarakter.put(c, frekuensiKarakter.getOrDefault(c, 0) + 1);
            }

            Node2 root = buildHuffmanTree(frekuensiKarakter);

            // Membuat tabel kode Huffman
            Map<Character, String> tabelKode = new HashMap<>();
            generateCodes(root, "", tabelKode);
            fullPath = userDirectory + "/" + nameFile + ".her";
            fileOutputStream = new FileOutputStream(fullPath);
            fileOutputStream.write(encodeHuffman(base64Encoded, tabelKode).getBytes());
            System.out.println("path file : " + fullPath);

            fullPath = userDirectory + "/" + nameFile + "-key.her";
            saveHuffmanTree(root, fullPath);

            System.out.println("success extract file");
        } catch (IOException e) {
            System.err.println("File path is incorrect or encoding failed.");
        }
        return base64Encoded;
    }

    void decodeFile(String filePath, String pathFileTree, String nameFile) {
        try {
            Node2 restoredFile = loadHuffmanTree(pathFileTree);
            //make tableTree
            String encodedContent = readFileContent(filePath);
            //readfile filePath (file extract)
            base64Decoded = decodeHuffman(restoredFile, encodedContent);
            //decoded data

            fileData = Base64.getDecoder().decode(base64Decoded);
            fullPath = userDirectory + "/" + nameFile + ".png";
            fileOutputStream = new FileOutputStream(fullPath);
            fileOutputStream.write(fileData);
            fileOutputStream.close();
            System.out.println("file path : " + fullPath);
        } catch (IOException e) {
            System.err.println("Decoding failed or file path is incorrect.");
        }
    }
}
