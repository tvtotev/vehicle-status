package com.softavail.examination.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Insurance {

    @JsonProperty("report")
    private final InsuranceReport report;

    @JsonCreator
    public Insurance(@JsonProperty("report") InsuranceReport report) {
        this.report = report;
    }

    public InsuranceReport getReport() {
        return report;
    }

}
