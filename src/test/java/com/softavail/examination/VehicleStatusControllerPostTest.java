package com.softavail.examination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;


/**
 * Simple controller test (POST)
 */
@MicronautTest
class VehicleStatusControllerPostTest {

    @Inject
    VehicleStatusClient vehicleStatusClient;

    @Test
    void postWithRequestBodyAndPostMappingWorks() {

        Set<String> features = Collections.unmodifiableSet(new HashSet<>());

        VehicleStatusRequest request = new VehicleStatusRequest("vin123", features);
        VehicleStatus response = vehicleStatusClient.check(request);
        assertNotNull(response);
        assertNotNull(request.getFeatures());
        assertEquals(request.getVin(), response.getVin());
    }
}
