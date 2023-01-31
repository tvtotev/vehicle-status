package com.softavail.examination.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.micronaut.validation.Validated;

@Validated
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
    private final Boolean isAccidentFree;
    private final MaintenanceScore maintenanceScores;

    @JsonCreator
    public VehicleStatus(@JsonProperty("vin") String vin,
            @JsonProperty("maintenanceScore") MaintenanceScore maintenanceScores,
            @JsonProperty("accidentFree") Boolean isAccidentFree) {
        this.id = UUID.randomUUID();
        this.vin = vin;
        this.maintenanceScores = maintenanceScores;
        this.isAccidentFree = isAccidentFree;
    }

    public String getId() {
        return id.toString();
    }

    public String getVin() {
        return vin;
    }

    public Boolean isAccidentFree() {
        return isAccidentFree;
    }

    public MaintenanceScore getMaintenanceScores() {
        return maintenanceScores;
    }
}
