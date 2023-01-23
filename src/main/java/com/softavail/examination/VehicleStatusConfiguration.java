package com.softavail.examination;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("vahicle-status")
public class VehicleStatusConfiguration {

    private String vin = "4Y1SL65848Z411439";

    public String getVin() {
        return vin;
    }

    public void setTemplate(String vin) {
        this.vin = vin;
    }
}