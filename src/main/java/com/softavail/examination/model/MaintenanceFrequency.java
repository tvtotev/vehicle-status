package com.softavail.examination.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MaintenanceFrequency {

    @JsonProperty(value="maintenance_frequency", required = true)
    private final String maintenanceFrequency;

    @JsonCreator
    public MaintenanceFrequency(@JsonProperty("maintenance_frequency") String maintenanceFrequency) {
        this.maintenanceFrequency = maintenanceFrequency;
    }

    public String getMaintenanceFrequency() {
        return maintenanceFrequency;
    }
}
