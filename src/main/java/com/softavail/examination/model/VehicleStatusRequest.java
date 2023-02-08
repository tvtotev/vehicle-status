package com.softavail.examination.model;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.validation.Validated;

@Validated
@Introspected
public class VehicleStatusRequest {

    public enum Feature {

        accident_free("accident_free"), maintenance("maintenance");

        public final String label;

        Feature(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

    }

    @NotBlank(message = "VIN cannot be empty/null")
    @Size(min = 3, max = 64, message = "VIN Length must be 3-64 chars")
    private final String vin;

    private final Set<Feature> features;

    @JsonCreator
    public VehicleStatusRequest(@JsonProperty("vin") String vin, @JsonProperty("features") Set<Feature> features) {
        this.vin = vin;
        this.features = features;
    }

    public String getVin() {
        return vin;
    }

    public Set<Feature> getFeatures() {
        return features;
    }

}
