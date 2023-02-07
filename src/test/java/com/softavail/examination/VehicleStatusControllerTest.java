package com.softavail.examination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

import com.softavail.examination.clients.VehicleStatusClient;
import com.softavail.examination.model.VehicleStatus;

import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * Simple controller test (GET)
 */
@MicronautTest
class VehicleStatusControllerTest {

    @Inject
    VehicleStatusClient vehicleStatusClient;

    @Test
    void testVehicleStatusService() {
        Publisher<VehicleStatus> response = vehicleStatusClient.check("4Y1SL65848Z411439");
        Mono<VehicleStatus> vehicleStatusMono = Publishers.convertPublisher(response, Mono.class);
        assertNotNull(response);
        assertNotNull(vehicleStatusMono);

        VehicleStatus vehicleStatus = vehicleStatusMono.block();
        assertNotNull(vehicleStatus);
        assertEquals("4Y1SL65848Z411439", vehicleStatus.getVin());
    }
}
