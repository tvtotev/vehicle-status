package com.softavail.examination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.softavail.examination.clients.VehicleStatusClient;
import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * Simple controller test (POST)
 */
@MicronautTest
class VehicleStatusControllerPostTest {

    @Inject
    VehicleStatusClient vehicleStatusClient;

    @Test
    void postWithRequestBodyAndPostMappingWorks() {

        Set<VehicleStatusRequest.Feature> features = Collections.unmodifiableSet(new HashSet<>());
        VehicleStatusRequest request = new VehicleStatusRequest("vin123", features);

        Mono<VehicleStatus> response = vehicleStatusClient.check(request);
        assertNotNull(response);

        VehicleStatus vehicleStatus = response.block(Duration.of(1000, ChronoUnit.MILLIS));
        assertNotNull(response);
        assertNotNull(request.getFeatures());
        assertEquals(vehicleStatus.getVin(), vehicleStatus.getVin());
    }
}
