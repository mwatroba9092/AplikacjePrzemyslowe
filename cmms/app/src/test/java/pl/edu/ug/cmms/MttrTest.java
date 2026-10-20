package pl.edu.ug.cmms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.edu.ug.cmms.Machine.MachineType.CNC_LATHE;
import static pl.edu.ug.cmms.WorkOrder.Priority.NORMAL;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderStatus.*;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderType.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;
import pl.edu.ug.cmms.WorkOrder.WorkOrderType;

class MttrTest {

    @Test
    void averagesRepairTimesPerHallUsingManuallyCalculatedExpectedValues() {
        List<Machine> machines = List.of(
                machine("M-01", "A"),
                machine("M-02", "A"),
                machine("M-03", "B")
        );
        List<WorkOrder> orders = List.of(
                order("WO-01", "M-01", BREAKDOWN, DONE,
                        "2026-01-10T08:00", "2026-01-10T09:00"),
                order("WO-02", "M-01", BREAKDOWN, DONE,
                        "2026-01-11T08:00", "2026-01-11T11:00"),
                order("WO-03", "M-02", BREAKDOWN, DONE,
                        "2026-01-10T08:00", "2026-01-10T16:00"),
                order("WO-04", "M-03", BREAKDOWN, DONE,
                        "2026-01-10T08:00", "2026-01-10T09:30")
        );

        // Recznie: hala A: (1 h + 3 h + 8 h) / 3 = 4 h.
        // Hala B: jedna naprawa trwajaca 90 min.
        // Liczymy srednia z napraw, a nie srednia ze srednich maszyn.
        Map<String, Duration> expected = Map.of(
                "A", Duration.ofHours(4),
                "B", Duration.ofMinutes(90)
        );

        assertEquals(
                expected,
                MaintenanceReports.calculateMttrByHall(machines, orders)
        );
    }

    @Test
    void includesOnlyCompletedBreakdowns() {
        List<Machine> machines = List.of(machine("M-01", "A"));
        List<WorkOrder> orders = List.of(
                order("WO-01", "M-01", BREAKDOWN, DONE,
                        "2026-01-10T08:00", "2026-01-10T10:00"),
                order("WO-02", "M-01", BREAKDOWN, OPEN,
                        "2026-01-11T08:00", null),
                order("WO-03", "M-01", BREAKDOWN, IN_PROGRESS,
                        "2026-01-12T08:00", null),
                order("WO-04", "M-01", BREAKDOWN, CANCELLED,
                        "2026-01-13T08:00", null),
                order("WO-05", "M-01", PREVENTIVE, DONE,
                        "2026-01-14T08:00", "2026-01-14T18:00"),
                order("WO-06", "M-01", INSPECTION, DONE,
                        "2026-01-15T08:00", "2026-01-15T16:00")
        );

        assertEquals(
                Map.of("A", Duration.ofHours(2)),
                MaintenanceReports.calculateMttrByHall(machines, orders)
        );
    }

    @Test
    void handlesRepairLastingMoreThanOneDay() {
        List<Machine> machines = List.of(machine("M-01", "A"));
        List<WorkOrder> orders = List.of(
                order("WO-01", "M-01", BREAKDOWN, DONE,
                        "2026-01-01T23:30", "2026-01-03T01:00")
        );

        // Recznie: 24 h do 2 stycznia 23:30 i jeszcze 1 h 30 min.
        assertEquals(
                Map.of("A", Duration.ofMinutes(1530)),
                MaintenanceReports.calculateMttrByHall(machines, orders)
        );
    }

    @Test
    void preservesSecondsUntilAfterCalculatingAverage() {
        List<Machine> machines = List.of(machine("M-01", "A"));
        List<WorkOrder> orders = List.of(
                order("WO-01", "M-01", BREAKDOWN, DONE,
                        "2026-01-10T08:00:00", "2026-01-10T08:01:50"),
                order("WO-02", "M-01", BREAKDOWN, DONE,
                        "2026-01-10T09:00:00", "2026-01-10T09:02:50")
        );

        // Recznie: (110 s + 170 s) / 2 = 140 s, czyli 2 min 20 s.
        // Odrzucenie sekund przed srednia daloby bledny wynik.
        assertEquals(
                Map.of("A", Duration.ofSeconds(140)),
                MaintenanceReports.calculateMttrByHall(machines, orders)
        );
    }

    @Test
    void returnsNoAverageWhenThereAreNoCompletedBreakdowns() {
        List<Machine> machines = List.of(
                machine("M-01", "A"),
                machine("M-02", "B")
        );
        List<WorkOrder> orders = List.of(
                order("WO-01", "M-01", BREAKDOWN, OPEN,
                        "2026-01-10T08:00", null),
                order("WO-02", "M-02", INSPECTION, DONE,
                        "2026-01-10T08:00", "2026-01-10T10:00")
        );

        assertEquals(
                Map.of(),
                MaintenanceReports.calculateMttrByHall(machines, orders)
        );
    }

    @Test
    void includesZeroLengthCompletedRepair() {
        List<Machine> machines = List.of(machine("M-01", "A"));
        List<WorkOrder> orders = List.of(
                order("WO-01", "M-01", BREAKDOWN, DONE,
                        "2026-01-10T08:00", "2026-01-10T08:00")
        );

        assertEquals(
                Map.of("A", Duration.ZERO),
                MaintenanceReports.calculateMttrByHall(machines, orders)
        );
    }

    @Test
    void rejectsCompletedBreakdownForUnknownMachine() {
        List<Machine> machines = List.of(machine("M-01", "A"));
        List<WorkOrder> orders = List.of(
                order("WO-01", "M-99", BREAKDOWN, DONE,
                        "2026-01-10T08:00", "2026-01-10T10:00")
        );

        assertThrows(IllegalArgumentException.class,
                () -> MaintenanceReports.calculateMttrByHall(machines, orders));
    }

    @Test
    void sampleDataHasExpectedMttrInFullMinutes() {
        Map<String, Duration> result = MaintenanceReports.calculateMttrByHall(
                SampleData.machines(), SampleData.workOrders()
        );

        assertEquals(3, result.size());
        assertEquals(831L, result.get("A").toMinutes());  // 13 h 51 min
        assertEquals(786L, result.get("B").toMinutes());  // 13 h 6 min
        assertEquals(1005L, result.get("C").toMinutes()); // 16 h 45 min
    }

    private static Machine machine(String id, String hall) {
        return new Machine(
                id, "Maszyna testowa", CNC_LATHE, hall, LocalDate.of(2020, 1, 15)
        );
    }

    private static WorkOrder order(
            String id,
            String machineId,
            WorkOrderType type,
            WorkOrderStatus status,
            String reportedAt,
            String closedAt
    ) {
        return new WorkOrder(
                id, machineId, type, NORMAL, status,
                LocalDateTime.parse(reportedAt),
                closedAt == null ? null : LocalDateTime.parse(closedAt)
        );
    }
}
