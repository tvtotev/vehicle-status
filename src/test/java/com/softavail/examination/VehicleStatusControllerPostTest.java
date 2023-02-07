package com.softavail.examination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

import com.softavail.examination.clients.VehicleStatusClient;
import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;

import io.micronaut.core.async.publisher.Publishers;
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

        Set<String> features = Collections.unmodifiableSet(new HashSet<>());
        VehicleStatusRequest request = new VehicleStatusRequest("vin123", features);

        Publisher<VehicleStatus> response = vehicleStatusClient.check(request);
        Mono<VehicleStatus> vehicleStatusMono = Publishers.convertPublisher(response, Mono.class);
        assertNotNull(response);
        assertNotNull(vehicleStatusMono);

        VehicleStatus vehicleStatus = vehicleStatusMono.block();
        assertNotNull(response);
        assertNotNull(request.getFeatures());
        assertEquals(vehicleStatus.getVin(), vehicleStatus.getVin());
    }
}
