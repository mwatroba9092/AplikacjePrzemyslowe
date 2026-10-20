package pl.edu.ug.cmms;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

@ResourceLock(Resources.SYSTEM_OUT)
class AppTest {

    @Test
    void printsRequiredSampleDataResults() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try (PrintStream capturedOut =
                     new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            App.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        // Dopuszczamy "zlecen" i "zleceń" w podpisie liczby zlecen.
        List<String> lines = buffer.toString(StandardCharsets.UTF_8)
                .replace('ń', 'n')
                .lines()
                .toList();

        List<String> expectedLines = List.of(
                "Liczba maszyn: 9",
                "Liczba zlecen serwisowych: 45",
                "CRITICAL: 0",
                "HIGH: 3",
                "NORMAL: 1",
                "LOW: 1",
                "M-04 (5)",
                "M-08 (4)",
                "M-05 (3)",
                "Hala A: 13 h 51 min",
                "Hala B: 13 h 6 min",
                "Hala C: 16 h 45 min"
        );

        for (String expectedLine : expectedLines) {
            assertTrue(
                    lines.contains(expectedLine),
                    "Brak oczekiwanej linii w raporcie: " + expectedLine
            );
        }
    }
}
