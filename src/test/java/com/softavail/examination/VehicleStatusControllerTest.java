package com.softavail.examination;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class VehicleStatusControllerTest {

    @Inject
    VehicleStatusClient vehicleStatusClient;

    @Test
    void testVehicleStatusService() {
        assertEquals(
                "4Y1SL65848Z411439",
                vehicleStatusClient.check("4Y1SL65848Z411439").getVin()
        );
    }
}
