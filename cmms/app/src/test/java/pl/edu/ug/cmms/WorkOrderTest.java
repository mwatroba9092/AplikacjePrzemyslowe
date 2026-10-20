package pl.edu.ug.cmms;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.edu.ug.cmms.WorkOrder.Priority.NORMAL;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderStatus.DONE;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderStatus.OPEN;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderType.BREAKDOWN;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;

class WorkOrderTest {

    private static final LocalDateTime REPORTED_AT =
            LocalDateTime.of(2026, 1, 10, 8, 0);
    private static final LocalDateTime CLOSED_AT =
            LocalDateTime.of(2026, 1, 10, 10, 0);

    @Test
    void acceptsValidCompletedOrder() {
        assertDoesNotThrow(() -> new WorkOrder(
                "WO-01", "M-01", BREAKDOWN, NORMAL, DONE, REPORTED_AT, CLOSED_AT
        ));
    }

    @ParameterizedTest
    @EnumSource(value = WorkOrderStatus.class,
            names = {"OPEN", "IN_PROGRESS", "CANCELLED"})
    void acceptsUnclosedNonDoneOrder(WorkOrderStatus status) {
        assertDoesNotThrow(() -> new WorkOrder(
                "WO-01", "M-01", BREAKDOWN, NORMAL, status, REPORTED_AT, null
        ));
    }

    // Wymagany przypadek 1: pusty identyfikator.
    @Test
    void rejectsEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder(
                "", "M-01", BREAKDOWN, NORMAL, OPEN, REPORTED_AT, null
        ));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingOrBlankId(String id) {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder(
                id, "M-01", BREAKDOWN, NORMAL, OPEN, REPORTED_AT, null
        ));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsInvalidMachineId(String machineId) {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder(
                "WO-01", machineId, BREAKDOWN, NORMAL, OPEN, REPORTED_AT, null
        ));
    }

    // Wymagany przypadek 2: DONE bez daty zamkniecia.
    @Test
    void rejectsDoneWithoutClosingTime() {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder(
                "WO-01", "M-01", BREAKDOWN, NORMAL, DONE, REPORTED_AT, null
        ));
    }

    // Wymagany przypadek 3: zamkniecie przed zgloszeniem.
    @Test
    void rejectsClosingBeforeReport() {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder(
                "WO-01", "M-01", BREAKDOWN, NORMAL, DONE,
                REPORTED_AT, REPORTED_AT.minusSeconds(1)
        ));
    }

    @Test
    void acceptsClosingAtReportTime() {
        assertDoesNotThrow(() -> new WorkOrder(
                "WO-01", "M-01", BREAKDOWN, NORMAL, DONE, REPORTED_AT, REPORTED_AT
        ));
    }

    @Test
    void rejectsMissingReportTime() {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder(
                "WO-01", "M-01", BREAKDOWN, NORMAL, OPEN, null, null
        ));
    }

    @Test
    void rejectsMissingType() {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder(
                "WO-01", "M-01", null, NORMAL, OPEN, REPORTED_AT, null
        ));
    }

    @Test
    void rejectsMissingPriority() {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder(
                "WO-01", "M-01", BREAKDOWN, null, OPEN, REPORTED_AT, null
        ));
    }

    @Test
    void rejectsMissingStatus() {
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder(
                "WO-01", "M-01", BREAKDOWN, NORMAL, null, REPORTED_AT, null
        ));
    }
}
