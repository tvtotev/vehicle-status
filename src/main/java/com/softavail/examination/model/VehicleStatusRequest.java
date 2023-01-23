package com.softavail.examination.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VehicleStatusRequest {

    public enum Feature {

        ACCIDENT_FREE("accident_free"), MAINTANANCE("maintenance");

        public final String label;

        Feature(String label) {
            this.label = label;
        }

        public String toString() {
            return label;
        }
    }

    private final String vin;
    private final Set<String> features;

    @JsonCreator
    public VehicleStatusRequest(@JsonProperty("vin") String vin, @JsonProperty("features") Set<String> features) {
        this.vin = vin;
        this.features = features;
    }

    public String getVin() {
        return vin;
    }

    public Set<String> getFeatures() {
        return features;
    }
    
}
