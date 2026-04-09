package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MostActiveCookie {

    public static void main(String[] args) {
        try {
            Arguments parsedArgs = parseArgs(args);
            List<String> result = findMostActiveCookies(parsedArgs.filePath(), parsedArgs.targetDate());

            for (String cookie : result) {
                System.out.println(cookie);
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println("Usage: java MostActiveCookie <cookie_log.csv> -d <YYYY-MM-DD>");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
            System.exit(1);
        }
    }

    public static List<String> findMostActiveCookies(String filePath, LocalDate targetDate) throws IOException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        int maxCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line = reader.readLine(); // skip header
            if (line == null) {
                return List.of();
            }

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",", 2);
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid CSV line: " + line);
                }

                String cookie = parts[0];
                LocalDate logDate = OffsetDateTime.parse(parts[1]).toLocalDate();

                if (logDate.isAfter(targetDate)) {
                    continue;
                }

                if (logDate.isBefore(targetDate)) {
                    break; // file is sorted newest to oldest
                }

                int newCount = counts.getOrDefault(cookie, 0) + 1;
                counts.put(cookie, newCount);
                maxCount = Math.max(maxCount, newCount);
            }
        }

        List<String> mostActiveCookies = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == maxCount) {
                mostActiveCookies.add(entry.getKey());
            }
        }

        return mostActiveCookies;
    }


    public static Arguments parseArgs(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Invalid number of arguments.");
        }

        String filePath = args[0];
        if (!"-d".equals(args[1])) {
            throw new IllegalArgumentException("Missing -d flag.");
        }

        LocalDate targetDate;
        try {
            targetDate = LocalDate.parse(args[2]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD.");
        }

        return new Arguments(filePath, targetDate);
    }

    record Arguments(String filePath, LocalDate targetDate) {}
}
