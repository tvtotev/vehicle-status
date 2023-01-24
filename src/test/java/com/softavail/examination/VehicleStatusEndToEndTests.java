package com.softavail.examination;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.softavail.examination.model.Insurance;
import com.softavail.examination.model.InsuranceReport;
import com.softavail.examination.model.MaintenanceFrequency;
import com.softavail.examination.model.MaintenanceFrequencyType;
import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatus.MaintenanceScore;
import com.softavail.examination.model.VehicleStatusRequest;
import com.softavail.examination.model.VehicleStatusRequest.Feature;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Integration tests for the vehicle-checker service
 */
@MicronautTest (rebuildContext = true)
@Property(name = "endpoint.insurance", value = VehicleStatusEndToEndTests.SITE_SHEMA + "://" + VehicleStatusEndToEndTests.SITE)
@Property(name = "endpoint.maintenance.frequency", value = "http://localhost")
public class VehicleStatusEndToEndTests {

    @Inject
    VehicleStatusClient vehicleStatusClient;

    public static final String SITE_SHEMA = "http";
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
    public WireMockRule wireMockRule = new WireMockRule(wireMockPort);
//    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());
    private static WireMockServer wireMockServer;
//    private WireMockServer wireMockServer = new WireMockServer();
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    
    private static Insurance insurance = null;
    private static String insuranceStr = null;
    private static MaintenanceFrequency maintenanceFrequency = null;
    private static String maintenanceFrequencyStr = null;
    
    
    @BeforeAll
    public static void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        InsuranceReport report = new InsuranceReport(2);
        insurance = new Insurance(report);
        try {
            insuranceStr = objectMapper.writeValueAsString(insurance);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        maintenanceFrequency = new MaintenanceFrequency(MaintenanceFrequencyType.verylow.toString());
        try {
            maintenanceFrequencyStr = objectMapper.writeValueAsString(maintenanceFrequency);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    
    @Test
    public void givenProgrammaticallyManagedServer_whenUsingSimpleStubbing_thenCorrect() throws IOException {
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
        
        configureFor(SITE_SHEMA, SITE, wireMockPort);
        stubFor(get(urlEqualTo(INSURANCE_PATH))
                .willReturn(aResponse().withBody(insuranceStr).withHeader("Content-Type", "application/json")));
        stubFor(get(urlEqualTo(FREQ_MAINT_PATH)).willReturn(
                aResponse().withBody(maintenanceFrequencyStr).withHeader("Content-Type", "application/json")));        

        HttpGet request;
        HttpResponse httpResponse;
        String stringResponse;
        
        request= new HttpGet(SITE_SHEMA + "://" + SITE + ":" + wireMockPort + INSURANCE_PATH);
        httpResponse = httpClient.execute(request);
        stringResponse = convertResponseToString(httpResponse);
        verify(getRequestedFor(urlEqualTo(INSURANCE_PATH)));
        assertEquals(insuranceStr, stringResponse);

        request= new HttpGet(SITE_SHEMA + "://" + SITE + ":" + wireMockPort + FREQ_MAINT_PATH);
        httpResponse = httpClient.execute(request);
        stringResponse = convertResponseToString(httpResponse);
        verify(getRequestedFor(urlEqualTo(FREQ_MAINT_PATH)));
        assertEquals(maintenanceFrequencyStr, stringResponse);

        postWithRequestBodyAndPostMappingWorks(VIN);
        
        wireMockServer.stop();
    }
    
    

    private static String convertResponseToString(HttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(responseStream, "UTF-8");
        String stringResponse = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return stringResponse;
    }
    
    void postWithRequestBodyAndPostMappingWorks(String vin) {
        Set<String> features = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(Feature.ACCIDENT_FREE.toString(), Feature.MAINTANANCE.toString())));

        VehicleStatusRequest request = new VehicleStatusRequest(vin, features);
        VehicleStatus response = vehicleStatusClient.check(request);
        assertNotNull(response);
        assertNotNull(request.getFeatures());
        assertEquals(request.getVin(), response.getVin());
        assertEquals(MaintenanceScore.POOR, response.getMaintenanceScores());
        assertEquals(Boolean.FALSE, response.isAccidentFree());
        
    }

}