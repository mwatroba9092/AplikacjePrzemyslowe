package pl.edu.ug.cmms;

import java.time.LocalDate;
import java.util.jar.Attributes.Name;

public record Machine(
    String id,
    String name,
    MachineType type,
    String hall,
    LocalDate installedOn
) {

    public Machine {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                "Id maszyny nie moze byc puste"
            );
        }

        if (name == null || id.isBlank()) {
            throw new IllegalArgumentException(
                "Nazwa maszyny nie moze byc pusta"
            );
        }

        if (type == null) {
            throw new IllegalArgumentException(
                "Typ maszyny jest wymagany"
            );
        }

        if (hall == null || id.isBlank()) {
            throw new IllegalArgumentException(
                "Hala maszyny nie moze byc pusta"
            );
        }

        if (installedOn == null) {
            throw new IllegalArgumentException(
                "Data instalacji maszyny jest wymagana"
            );
        }
    }

    public enum MachineType {
        CNC_LATHE,
        CONVEYOR,
        MILLING_MACHINE,
        WELDING_ROBOT,
        INJECTION_MOLDER,
        HYDRAULIC_PRESS,
        COMPRESSOR
    }
}
