package com.softavail.examination.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InsuranceReport {

    private final int claims;

    @JsonCreator
    public InsuranceReport(@JsonProperty("claims") int claims) {
        this.claims = claims;
    }

    public int getClaims() {
        return claims;
    }

}
