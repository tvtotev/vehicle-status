package com.softavail.examination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.softavail.examination.clients.VehicleStatusClient;
import com.softavail.examination.model.VehicleStatus;

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
        Mono<VehicleStatus> response = vehicleStatusClient.check("4Y1SL65848Z411439");
        assertNotNull(response);

        VehicleStatus vehicleStatus = response.block();
        assertNotNull(vehicleStatus);
        assertEquals("4Y1SL65848Z411439", vehicleStatus.getVin());
    }
}
