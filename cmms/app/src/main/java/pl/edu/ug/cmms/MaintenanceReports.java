package pl.edu.ug.cmms;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import pl.edu.ug.cmms.WorkOrder.Priority;
import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;

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
}