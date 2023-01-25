package com.softavail.examination;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
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
import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;
import com.softavail.examination.model.VehicleStatusRequest.Feature;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Integration tests for the vehicle-checker service
 */
@MicronautTest(rebuildContext = true)
// Use HTTPS in order to provoke SSL exception/Service unavailable
@Property(name = "endpoint.insurance", value = VehicleStatusEndToEndServiceUnavailableTests.SITE_SHEMA_SECURE + "://"
        + VehicleStatusEndToEndServiceUnavailableTests.SITE)
//Use HTTPS in order to provoke SSL exception/Service unavailable
@Property(name = "endpoint.maintenance.frequency", value = VehicleStatusEndToEndServiceUnavailableTests.SITE_SHEMA_SECURE
        + "://" + VehicleStatusEndToEndServiceUnavailableTests.SITE)
public class VehicleStatusEndToEndServiceUnavailableTests {

    @Inject
    VehicleStatusClient vehicleStatusClient;

    public static final String SITE_SHEMA = "http";
    public static final String SITE_SHEMA_SECURE = "https"; // httpS should cause exception
    public static final String SITE = "localhost";

    private static String VIN = "4Y1SL65848Z411439";
    private static final String INSURANCE_PATH = "/accidents/report?vin=" + VIN;
    private static final String FREQ_MAINT_PATH = "/cars/" + VIN;

    @Property(name = "endpoint.insurance")
    private String insuranceEndpoint;

    @Property(name = "endpoint.maintenance.frequency")
    private String maintenanceFrequencyEndpoint;

    private static int wireMockPort = 80;

    @Rule
    private static WireMockRule wireMockRule = new WireMockRule(wireMockPort);
    private static WireMockServer wireMockServer;

    @BeforeAll
    static void init() {
        // Start WireMock
        if (!wireMockRule.isRunning()) {
            wireMockRule.start();
        }
        wireMockPort = wireMockRule.port();
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
        configureFor(SITE_SHEMA, SITE, wireMockPort);
        stubFor(get(urlEqualTo(INSURANCE_PATH)).willReturn(aResponse().withBody((String) null)));
        stubFor(get(urlEqualTo(FREQ_MAINT_PATH)).willReturn(aResponse().withBody((String) null)));
    }

    /**
     * Integration test for negative scenario - service unavailability
     */
    @Test
    void endToEndPositiveTestWithRequestingInsuranceAndMaintenanceServices() {
        // Perform end-to-end test with external service unavailability
        Set<String> features = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(Feature.ACCIDENT_FREE.toString(), Feature.MAINTANANCE.toString())));

        VehicleStatusRequest request = new VehicleStatusRequest(VIN, features);
        VehicleStatus response;
        try {
            response = vehicleStatusClient.check(request);
            assertNull(response);
        } catch (HttpClientResponseException e) {
            assertEquals(e.getStatus(), HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

}