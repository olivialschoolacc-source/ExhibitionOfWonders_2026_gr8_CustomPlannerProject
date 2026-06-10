package calendar.main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class BulletJournal {
    private final List<String> entries;

    public BulletJournal() {
        entries = new ArrayList<>();
    }

    public List<String> getEntries() {
        return new ArrayList<>(entries);
    }

    public void addEntry(String entry) {
        if (entry == null || entry.trim().isEmpty()) {
            return;
        }
        entries.add(entry.trim());
    }

    public void updateEntry(int index, String entry) {
        if (entry == null || index < 0 || index >= entries.size()) {
            return;
        }
        entries.set(index, entry.trim());
    }

    public void removeEntry(int index) {
        if (index < 0 || index >= entries.size()) {
            return;
        }
        entries.remove(index);
    }

    public void clearEntries() {
        entries.clear();
    }

    public void saveToFile(Path path) throws IOException {
        List<String> encoded = entries.stream()
                .map(this::encode)
                .collect(Collectors.toList());
        Files.write(path, encoded, StandardCharsets.UTF_8);
    }

    public void loadFromFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        List<String> encoded = Files.readAllLines(path, StandardCharsets.UTF_8);
        entries.clear();
        for (String line : encoded) {
            if (!line.isEmpty()) {
                entries.add(decode(line));
            }
        }
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}