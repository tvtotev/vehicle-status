package com.softavail.examination;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest(startApplication = true)
class VehicleStatusServiceTest {

    @Inject
    VehicleStatusService vehicleStatusService;

    @Test
    void serviceValidation() {
        assertDoesNotThrow(() -> vehicleStatusService.check("foo"));
    }
}