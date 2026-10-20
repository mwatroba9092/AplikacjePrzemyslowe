package pl.edu.ug.cmms;

import java.util.List;
import java.util.Map;
import java.time.Duration;
import java.util.Set;
import java.util.TreeSet;

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

        List<Map.Entry<String, Integer>> topMachines =
        MaintenanceReports.topMachinesByBreakdowns(workOrders);

        System.out.println();
        System.out.println("Trzy maszyny z najwieksza liczba awarii:");

        for (Map.Entry<String, Integer> entry : topMachines) {
            System.out.println(
                    entry.getKey() + " (" + entry.getValue() + ")"
            );
        }

        Map<String, Duration> mttrByHall =
        MaintenanceReports.calculateMttrByHall(machines, workOrders);

        Set<String> halls = new TreeSet<>();

        for (Machine machine : machines) {
            halls.add(machine.hall());
        }

        System.out.println();
        System.out.println("MTTR hal:");

        for (String hall : halls) {
            Duration mttr = mttrByHall.get(hall);

            if (mttr == null) {
                System.out.println(
                        "Hala " + hall + ": brak zakonczenia awarii"
                );
            } else {
                long totalMinutes = mttr.toMinutes();
                long hours = totalMinutes / 60;
                long minutes = totalMinutes % 60;

                System.out.println(
                        "Hala " + hall + ": " + hours + " h " + minutes + " min"
                );
            }
        }
    }
}