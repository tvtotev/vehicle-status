package com.softavail.examination;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.softavail.examination.clients.InsuranceClient;
import com.softavail.examination.clients.MaintenanceFrequencyClient;
import com.softavail.examination.clients.VehicleStatusClient;
import com.softavail.examination.model.Insurance;
import com.softavail.examination.model.InsuranceReport;
import com.softavail.examination.model.MaintenanceFrequency;
import com.softavail.examination.model.MaintenanceFrequencyType;
import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatus.MaintenanceScore;
import com.softavail.examination.model.VehicleStatusRequest;
import com.softavail.examination.model.VehicleStatusRequest.Feature;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the vehicle-checker service
 */
@MicronautTest(rebuildContext = true)
@Property(name = VehicleStatusEndToEndTests.INSURANCE_SERVICE, value = VehicleStatusEndToEndTests.SITE_SHEMA + "://"
        + VehicleStatusEndToEndTests.SITE_HOST)
@Property(name = VehicleStatusEndToEndTests.FREQ_MAINT_SERVICE, value = VehicleStatusEndToEndTests.SITE_SHEMA + "://"
        + VehicleStatusEndToEndTests.SITE_HOST)
public class VehicleStatusEndToEndTests {

    @Inject
    VehicleStatusClient vehicleStatusClient;

    public static final String SITE_SHEMA = "http";
    public static final String SITE_HOST = "localhost";

    public static final String INSURANCE_SERVICE = "micronaut.http.services." + InsuranceClient.SERVICE_NAME + ".url";
    public static final String FREQ_MAINT_SERVICE = "micronaut.http.services." + MaintenanceFrequencyClient.SERVICE_NAME
            + ".url";

    private static String VIN = "4Y1SL65848Z411439";

    private static final String INSURANCE_PATH = "/accidents/report?vin=" + VIN;
    private static final String FREQ_MAINT_PATH = "/cars/" + VIN;

    @Property(name = "micronaut.http.services.insurance.url")
    private String insuranceEndpoint;

    @Property(name = "micronaut.http.services.maintenance-frequency.url")
    private String maintenanceFrequencyEndpoint;

    private static int wireMockPort = 80;

    @Rule
    private static WireMockRule wireMockRule = new WireMockRule(wireMockPort);
    private static WireMockServer wireMockServer;
//    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());
//    private WireMockServer wireMockServer = new WireMockServer();
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    private static Insurance insurance = null;
    private static String insuranceStr = null;
    private static MaintenanceFrequency maintenanceFrequency = null;
    private static String maintenanceFrequencyStr = null;

    @BeforeAll
    static void init() {
        // Prepare response mocks for the external services
        ObjectMapper objectMapper = new ObjectMapper();

        // Insurance mock
        InsuranceReport report = new InsuranceReport(2);
        insurance = new Insurance(report);
        try {
            insuranceStr = objectMapper.writeValueAsString(insurance);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // Maintenance mock
        maintenanceFrequency = new MaintenanceFrequency(MaintenanceFrequencyType.verylow.toString());
        try {
            maintenanceFrequencyStr = objectMapper.writeValueAsString(maintenanceFrequency);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
        configureFor(SITE_SHEMA, SITE_HOST, wireMockPort);
        stubFor(get(urlEqualTo(INSURANCE_PATH)).willReturn(
                aResponse().withBody(insuranceStr).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));
        stubFor(get(urlEqualTo(FREQ_MAINT_PATH)).willReturn(aResponse().withBody(maintenanceFrequencyStr)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));

        HttpGet request;
        HttpResponse httpResponse;
        String stringResponse;

        // Check whether Insurance external service is mocked
        request = new HttpGet(SITE_SHEMA + "://" + SITE_HOST + ":" + wireMockPort + INSURANCE_PATH);
        httpResponse = httpClient.execute(request);
        stringResponse = convertResponseToString(httpResponse);
        verify(getRequestedFor(urlEqualTo(INSURANCE_PATH)));
        assertEquals(insuranceStr, stringResponse);

        // Check whether Maintenance external service is mocked
        request = new HttpGet(SITE_SHEMA + "://" + SITE_HOST + ":" + wireMockPort + FREQ_MAINT_PATH);
        httpResponse = httpClient.execute(request);
        stringResponse = convertResponseToString(httpResponse);
        verify(getRequestedFor(urlEqualTo(FREQ_MAINT_PATH)));
        assertEquals(maintenanceFrequencyStr, stringResponse);

    }

    /**
     * Converts response to String
     * 
     * @param response
     * @return Text of the response
     * @throws IOException
     */
    private static String convertResponseToString(HttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(responseStream, "UTF-8");
        String stringResponse = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return stringResponse;
    }

    /**
     * Integration test for positive scenario
     */
    @Test
    void endToEndPositiveTestWithRequestingInsuranceAndMaintenanceServices() throws IOException {
        // Perform end-to-end test
        Set<String> features = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(Feature.ACCIDENT_FREE.toString(), Feature.MAINTANANCE.toString())));
        VehicleStatusRequest request = new VehicleStatusRequest(VIN, features);
        Mono<VehicleStatus> response = vehicleStatusClient.check(request);
        assertNotNull(response);

        VehicleStatus vehicleStatus = response.block();
        assertNotNull(request.getFeatures());
        assertEquals(request.getVin(), vehicleStatus.getVin());
        assertEquals(MaintenanceScore.POOR, vehicleStatus.getMaintenanceScores());
        assertEquals(Boolean.FALSE, vehicleStatus.isAccidentFree());
    }

    /**
     * Integration test for negative scenario. Neither insurance you nor maintenance
     * is requested
     */
    @Test
    void postWithRequestBodyAndEmtyServiceList() {
        Set<String> features = Collections.unmodifiableSet(new HashSet<>(Arrays.asList()));
        VehicleStatusRequest request = new VehicleStatusRequest(VIN, features);
        Mono<VehicleStatus> response = vehicleStatusClient.check(request);
        assertNotNull(response);

        VehicleStatus vehicleStatus = response.block();
        assertNotNull(response);
        assertEquals(0, request.getFeatures().size());
        assertEquals(request.getVin(), vehicleStatus.getVin());
        assertNull(vehicleStatus.getMaintenanceScores());
        assertNull(vehicleStatus.isAccidentFree());
    }

}