package pl.edu.ug.cmms;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import java.util.TreeMap;

import pl.edu.ug.cmms.WorkOrder.Priority;
import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;
import pl.edu.ug.cmms.WorkOrder.WorkOrderType;

public final class MaintenanceReports {

    private MaintenanceReports() {

    }
    
    public static Map<Priority, Integer> countUnfinishedByPriority(
        List<WorkOrder> workOrders
    ) {
        Map<Priority, Integer> counts = new EnumMap<>(Priority.class);

        for (Priority priority : Priority.values()) {
            counts.put(priority, 0);
        }

        for (WorkOrder order : workOrders) {
            if (order.status() == WorkOrderStatus.OPEN
                    || order.status() == WorkOrderStatus.IN_PROGRESS) {

                Priority priority = order.priority();
                int currentCount = counts.get(priority);
                counts.put(priority, currentCount + 1);
            }
        }

        return counts;
    }

    public static List<Map.Entry<String, Integer>> topMachinesByBreakdowns(
        List<WorkOrder> workOrders
    ) {
    Map<String, Integer> breakdownCounts = new HashMap<>();

    for (WorkOrder order : workOrders) {
        if (order.type() == WorkOrderType.BREAKDOWN
                && order.status() != WorkOrderStatus.CANCELLED) {

            String machineId = order.machineId();
            int currentCount = breakdownCounts.getOrDefault(machineId, 0);
            breakdownCounts.put(machineId, currentCount + 1);
        }
    }

    return breakdownCounts.entrySet()
            .stream()
            .sorted(
                    Map.Entry.<String, Integer>comparingByValue()
                            .reversed()
                            .thenComparing(Map.Entry.comparingByKey())
            )
            .limit(3)
            .toList();
    }

    public static Map<String, Duration> calculateMttrByHall(
        List<Machine> machines,
        List<WorkOrder> workOrders
    ) {
        Map<String, String> hallByMachineId = new HashMap<>();

        for (Machine machine : machines) {
            hallByMachineId.put(machine.id(), machine.hall());
        }

        Map<String, Duration> totalTimes = new HashMap<>();
        Map<String, Integer> repairCounts = new HashMap<>();

        for (WorkOrder order : workOrders) {
            if (order.type() == WorkOrderType.BREAKDOWN
                    && order.status() == WorkOrderStatus.DONE) {

                String hall = hallByMachineId.get(order.machineId());

                if (hall == null) {
                    throw new IllegalArgumentException(
                            "Nieznana maszyna: " + order.machineId()
                    );
                }

                Duration repairTime = Duration.between(
                        order.reportedAt(),
                        order.closedAT()
                );

                Duration currentTotal =
                        totalTimes.getOrDefault(hall, Duration.ZERO);

                totalTimes.put(hall, currentTotal.plus(repairTime));

                int currentCount = repairCounts.getOrDefault(hall, 0);
                repairCounts.put(hall, currentCount + 1);
            }
        }

        Map<String, Duration> mttrByHall = new TreeMap<>();

        for (Map.Entry<String, Duration> entry : totalTimes.entrySet()) {
            String hall = entry.getKey();
            Duration totalTime = entry.getValue();
            int count = repairCounts.get(hall);

            Duration averageTime = totalTime.dividedBy(count);
            mttrByHall.put(hall, averageTime);
        }

        return mttrByHall;
        }
}