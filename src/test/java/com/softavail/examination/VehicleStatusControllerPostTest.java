package com.softavail.examination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;
import com.softavail.examination.model.VehicleStatusRequest.Feature;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest (rebuildContext = true)
@Property(name = "endpoint.insurance", value = VehicleStatusEndToEndTests.SITE_SHEMA + "://" + VehicleStatusEndToEndTests.SITE)
@Property(name = "endpoint.maintenance.frequency", value = "http://localhost")
@Disabled
class VehicleStatusControllerPostTest {

    @Inject
    VehicleStatusClient vehicleStatusClient;

    // A service
    @Test
    void postWithRequestBodyAndPostMappingWorks() {

        Set<String> features = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(Feature.ACCIDENT_FREE.toString(), Feature.MAINTANANCE.toString())));

        VehicleStatusRequest request = new VehicleStatusRequest("vin123", features);
        VehicleStatus response = vehicleStatusClient.check(request);
        assertNotNull(response);
        assertNotNull(request.getFeatures());
        assertEquals(request.getVin(), response.getVin());
    }
}