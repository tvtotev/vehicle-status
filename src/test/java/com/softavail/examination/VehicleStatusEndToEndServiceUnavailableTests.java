package com.softavail.examination;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.softavail.examination.clients.InsuranceClient;
import com.softavail.examination.clients.MaintenanceFrequencyClient;
import com.softavail.examination.clients.VehicleStatusClient;
import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;
import com.softavail.examination.model.VehicleStatusRequest.Feature;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the vehicle-checker service
 */
@MicronautTest(rebuildContext = true)
//Use HTTPS in order to provoke SSL exception -> Service unavailable
@Property(name = VehicleStatusEndToEndServiceUnavailableTests.INSURANCE_SERVICE, value = VehicleStatusEndToEndServiceUnavailableTests.SITE_SHEMA_SECURE
        + "://" + VehicleStatusEndToEndServiceUnavailableTests.SITE_HOST + ":"
        + VehicleStatusEndToEndServiceUnavailableTests.wireMockPort)
//Use HTTPS in order to provoke SSL exception -> Service unavailable
@Property(name = VehicleStatusEndToEndServiceUnavailableTests.FREQ_MAINT_SERVICE, value = VehicleStatusEndToEndServiceUnavailableTests.SITE_SHEMA_SECURE
        + "://" + VehicleStatusEndToEndServiceUnavailableTests.SITE_HOST + ":"
        + VehicleStatusEndToEndServiceUnavailableTests.wireMockPort)

public class VehicleStatusEndToEndServiceUnavailableTests {

    @Inject
    VehicleStatusClient vehicleStatusClient;

    public static final String SITE_SHEMA = "http";
    public static final String SITE_SHEMA_SECURE = "https"; // httpS should cause exception
    public static final String SITE_HOST = "localhost";

    public static final int wireMockPort = 8081;

    public static final String INSURANCE_SERVICE = "micronaut.http.services." + InsuranceClient.SERVICE_NAME + ".url";
    public static final String FREQ_MAINT_SERVICE = "micronaut.http.services." + MaintenanceFrequencyClient.SERVICE_NAME
            + ".url";

    private static String VIN = "4Y1SL65848Z411439";

    @Property(name = "micronaut.http.services.insurance.url")
    private String insuranceEndpoint;

    @Property(name = "micronaut.http.services.maintenance-frequency.url")
    private String maintenanceFrequencyEndpoint;

    @Rule
    private static WireMockRule wireMockRule = new WireMockRule(wireMockPort);
    private static WireMockServer wireMockServer;

    @BeforeAll
    static void init() {
        // Start WireMock
        if (!wireMockRule.isRunning()) {
            wireMockRule.start();
        }
        wireMockServer = new WireMockServer(wireMockPort);
        if (!wireMockServer.isRunning()) {
            try {
                wireMockServer.start();
            } catch (Exception e) {
            }
        }
    }

    @AfterAll
    static void shutdown() {
        wireMockRule.stop();
        wireMockServer.stop();
    }

    @BeforeEach
    void beforeEach() throws ClientProtocolException, IOException {
        // Mock responses: Insurance and Maintenance external services
        configureFor(SITE_SHEMA, SITE_HOST, wireMockPort);
    }

    /**
     * Integration test for negative scenario - service unavailability
     */
    @Test
    void endToEndPositiveTestWithRequestingInsuranceAndMaintenanceServices() {
        // Perform end-to-end test with external service unavailability
        Set<Feature> features = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(Feature.accident_free, Feature.maintenance)));

        VehicleStatusRequest request = new VehicleStatusRequest(VIN, features);
        Mono<VehicleStatus> response;
        try {
            response = vehicleStatusClient.check(request);
            assertNotNull(response);

            VehicleStatus vehicleStatus = response.block(Duration.of(1000, ChronoUnit.MILLIS));
            assertNull(vehicleStatus);
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, e.getStatus());
        }

    }

}