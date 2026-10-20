package pl.edu.ug.cmms;

import java.time.LocalDateTime;

public record WorkOrder(
    String id,
    String machineId,
    WorkOrderType type,
    Priority priority,
    WorkOrderStatus status,
    LocalDateTime reportedAt,
    LocalDateTime closedAT
) {

    public WorkOrder {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                "Id zlecenia nie moze byc puste"
            );
        }

        if (machineId == null || id.isBlank()) {
            throw new IllegalArgumentException(
                "Id maszyny nie moze byc puste"
            );
        }

        if (type == null) {
            throw new IllegalArgumentException(
                "Typ zlecenia jest wymagany"
            );
        }

        if (priority == null) {
            throw new IllegalArgumentException(
                "Prorytet zlecenia jest wymagany"
            );
        }

        if (status == null) {
            throw new IllegalArgumentException(
                "Status zlecenia jest wymagany"
            );
        }

        if (reportedAt == null) {
            throw new IllegalArgumentException(
                "Data zgłoszenia jest wymagana"
            );
        }

        if (status == WorkOrderStatus.DONE && closedAT == null) {
            throw new IllegalArgumentException(
                "Zakonczenie zlecenia musi miec date zamkniecia"
            );
        }

        if (closedAT != null && closedAT.isBefore(reportedAt)) {
            throw new IllegalArgumentException(
                "Data zamkniecia nie moze byc wczesniejsza niz data zgloszenia"
            );
        }
    }


    public enum WorkOrderType {
        PREVENTIVE,
        BREAKDOWN,
        INSPECTION
    }

    public enum Priority {
        CRITICAL,
        HIGH,
        NORMAL,
        LOW
    } 

    public enum WorkOrderStatus {
        OPEN,
        IN_PROGRESS,
        DONE,
        CANCELLED
    }
}