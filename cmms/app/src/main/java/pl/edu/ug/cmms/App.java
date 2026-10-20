package pl.edu.ug.cmms;

import java.util.List;
import java.util.Map;

import pl.edu.ug.cmms.WorkOrder.Priority;

public class App {
    public static void main(String[] args) {
        List<Machine> machines = SampleData.machines();
        List<WorkOrder> workOrders = SampleData.workOrders();

        System.out.println("Liczba maszyn: " + machines.size());
        System.out.println("Liczba zlecen serwisowych: " + workOrders.size());


        Map<Priority, Integer> unfinishedByPriority =
                MaintenanceReports.countUnfinishedByPriority(workOrders);

        System.out.println();
        System.out.println("Niezakonczone zlecnia wedlug priorytetu:");

        for (Priority priority : Priority.values()) {
            System.out.println(
                    priority + ": " + unfinishedByPriority.get(priority)
            );
        }
    }
}