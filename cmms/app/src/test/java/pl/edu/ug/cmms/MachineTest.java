package pl.edu.ug.cmms;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.edu.ug.cmms.Machine.MachineType.CNC_LATHE;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MachineTest {

    private static final LocalDate INSTALLATION_DATE = LocalDate.of(2020, 1, 15);

    @Test
    void acceptsValidMachine() {
        assertDoesNotThrow(() -> new Machine(
                "M-01", "Tokarka testowa", CNC_LATHE, "A", INSTALLATION_DATE
        ));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsInvalidId(String id) {
        assertThrows(IllegalArgumentException.class, () -> new Machine(
                id, "Tokarka testowa", CNC_LATHE, "A", INSTALLATION_DATE
        ));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsInvalidName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Machine(
                "M-01", name, CNC_LATHE, "A", INSTALLATION_DATE
        ));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsInvalidHall(String hall) {
        assertThrows(IllegalArgumentException.class, () -> new Machine(
                "M-01", "Tokarka testowa", CNC_LATHE, hall, INSTALLATION_DATE
        ));
    }

    @Test
    void rejectsMissingType() {
        assertThrows(IllegalArgumentException.class, () -> new Machine(
                "M-01", "Tokarka testowa", null, "A", INSTALLATION_DATE
        ));
    }

    @Test
    void rejectsMissingInstallationDate() {
        assertThrows(IllegalArgumentException.class, () -> new Machine(
                "M-01", "Tokarka testowa", CNC_LATHE, "A", null
        ));
    }
}
