package pl.edu.ug.cmms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.edu.ug.cmms.WorkOrder.Priority.*;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderStatus.*;
import static pl.edu.ug.cmms.WorkOrder.WorkOrderType.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.edu.ug.cmms.WorkOrder.Priority;
import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;
import pl.edu.ug.cmms.WorkOrder.WorkOrderType;

class MaintenanceReportsTest {

    private static final LocalDateTime REPORTED_AT =
            LocalDateTime.of(2026, 1, 10, 8, 0);

    @Test
    void countsOnlyOpenAndInProgressOrdersForEveryPriority() {
        List<WorkOrder> orders = List.of(
                order("WO-01", "M-01", BREAKDOWN, HIGH, OPEN),
                order("WO-02", "M-01", INSPECTION, HIGH, IN_PROGRESS),
                order("WO-03", "M-02", PREVENTIVE, NORMAL, OPEN),
                order("WO-04", "M-02", INSPECTION, LOW, OPEN),
                order("WO-05", "M-01", BREAKDOWN, CRITICAL, DONE),
                order("WO-06", "M-01", BREAKDOWN, CRITICAL, CANCELLED)
        );

        assertEquals(
                Map.of(CRITICAL, 0, HIGH, 2, NORMAL, 1, LOW, 1),
                MaintenanceReports.countUnfinishedByPriority(orders)
        );
    }

    @Test
    void returnsAllPrioritiesWithZeroForEmptyInput() {
        assertEquals(
                Map.of(CRITICAL, 0, HIGH, 0, NORMAL, 0, LOW, 0),
                MaintenanceReports.countUnfinishedByPriority(List.of())
        );
    }

    @Test
    void sampleDataHasExpectedUnfinishedCounts() {
        assertEquals(
                Map.of(CRITICAL, 0, HIGH, 3, NORMAL, 1, LOW, 1),
                MaintenanceReports.countUnfinishedByPriority(SampleData.workOrders())
        );
    }

    @Test
    void ranksThreeMachinesByBreakdownCountAndThenById() {
        List<WorkOrder> orders = List.of(
                order("WO-01", "M-04", BREAKDOWN, NORMAL, DONE),
                order("WO-02", "M-04", BREAKDOWN, NORMAL, OPEN),
                order("WO-03", "M-04", BREAKDOWN, NORMAL, IN_PROGRESS),
                order("WO-04", "M-03", BREAKDOWN, NORMAL, DONE),
                order("WO-05", "M-03", BREAKDOWN, NORMAL, OPEN),
                order("WO-06", "M-02", BREAKDOWN, NORMAL, DONE),
                order("WO-07", "M-02", BREAKDOWN, NORMAL, OPEN),
                order("WO-08", "M-01", BREAKDOWN, NORMAL, DONE),
                order("WO-09", "M-01", BREAKDOWN, NORMAL, OPEN),
                // Anulowane awarie nie powinny umiescic M-00 w rankingu.
                order("WO-10", "M-00", BREAKDOWN, NORMAL, CANCELLED),
                order("WO-11", "M-00", BREAKDOWN, NORMAL, CANCELLED),
                order("WO-12", "M-00", BREAKDOWN, NORMAL, CANCELLED),
                order("WO-13", "M-00", BREAKDOWN, NORMAL, CANCELLED),
                // Inne typy zlecen tez nie sa awariami.
                order("WO-14", "M-00", PREVENTIVE, NORMAL, DONE),
                order("WO-15", "M-00", INSPECTION, NORMAL, DONE)
        );

        assertEquals(
                List.of(
                        Map.entry("M-04", 3),
                        Map.entry("M-01", 2),
                        Map.entry("M-02", 2)
                ),
                MaintenanceReports.topMachinesByBreakdowns(orders)
        );
    }

    @Test
    void ranksFewerThanThreeMachinesWhenOnlyOneHasABreakdown() {
        List<WorkOrder> orders = List.of(
                order("WO-01", "M-01", BREAKDOWN, NORMAL, OPEN)
        );

        assertEquals(
                List.of(Map.entry("M-01", 1)),
                MaintenanceReports.topMachinesByBreakdowns(orders)
        );
    }

    @Test
    void returnsEmptyRankingForEmptyInput() {
        assertEquals(
                List.of(),
                MaintenanceReports.topMachinesByBreakdowns(List.of())
        );
    }

    @Test
    void sampleDataHasExpectedBreakdownRanking() {
        assertEquals(
                List.of(
                        Map.entry("M-04", 5),
                        Map.entry("M-08", 4),
                        Map.entry("M-05", 3)
                ),
                MaintenanceReports.topMachinesByBreakdowns(SampleData.workOrders())
        );
    }

    private static WorkOrder order(
            String id,
            String machineId,
            WorkOrderType type,
            Priority priority,
            WorkOrderStatus status
    ) {
        LocalDateTime closedAt =
                status == DONE ? REPORTED_AT.plusHours(1) : null;

        return new WorkOrder(
                id, machineId, type, priority, status, REPORTED_AT, closedAt
        );
    }
}
