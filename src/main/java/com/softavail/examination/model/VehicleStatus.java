package com.softavail.examination.model;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VehicleStatus {

    public enum MaintenanceScore {

        POOR("poor"), AVERAGE("average"), GOOD("good");

        public final String label;

        MaintenanceScore(String label) {
            this.label = label;
        }
    }

    private final UUID id;
    private final String vin;
    private final Boolean accidentFree;
    private final Set<MaintenanceScore> maintenanceScores;

    @JsonCreator
    public VehicleStatus(@JsonProperty("vin") String vin,
            @JsonProperty("maintenanceScore") Set<MaintenanceScore> maintenanceScores) {
        this.id = UUID.randomUUID();
        this.vin = vin;
        this.maintenanceScores = maintenanceScores;
        accidentFree = null;
    }

    public String getId() {
        return id.toString();
    }

    public String getVin() {
        return vin;
    }

    public Boolean accidentFree() {
        return accidentFree;
    }

    public Set<MaintenanceScore> getMaintenanceScores() {
        return maintenanceScores;
    }
}
