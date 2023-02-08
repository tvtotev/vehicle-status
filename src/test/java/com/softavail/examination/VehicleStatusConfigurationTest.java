package com.softavail.examination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.micronaut.context.BeanContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Simple controller test
 */
@MicronautTest(startApplication = false)
class VehicleStatusConfigurationTest {

    @Inject
    BeanContext beanContext;

    @Test
    void testSpringConfigurationProperties() {
        assertTrue(beanContext.containsBean(VehicleStatusConfiguration.class));
        VehicleStatusConfiguration vehicleStatusConfiguration = beanContext.getBean(VehicleStatusConfiguration.class);
        assertEquals("4Y1SL65848Z411439", vehicleStatusConfiguration.getVin());
    }
}
