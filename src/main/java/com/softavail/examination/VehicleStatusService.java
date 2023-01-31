package com.softavail.examination;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softavail.examination.model.Insurance;
import com.softavail.examination.model.MaintenanceFrequency;
import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatus.MaintenanceScore;
import com.softavail.examination.model.VehicleStatusRequest.Feature;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@Validated
public class VehicleStatusService {
    // https://guides.micronaut.io/latest/micronaut-http-client-maven-java.html
    private static final Logger LOG = LoggerFactory.getLogger(VehicleStatusService.class);

    private final AtomicReference<VehicleStatus> lastVehicleStatus = new AtomicReference<>();

    @Property(name = "endpoint.insurance")
    private String insuranceEndpoint;

    @Property(name = "endpoint.maintenance.frequency")
    private String maintenanceFrequencyEndpoint;

    @Inject
    @Client("/")
    HttpClient httpClient;

    public VehicleStatus check(String vin) {
        final VehicleStatus vehicleStatus = new VehicleStatus(vin, null, false);
        lastVehicleStatus.set(vehicleStatus);
        return vehicleStatus;
    }

    public VehicleStatus check(String vin, Set<String> features) throws ServiceUnavailableException {
        final VehicleStatus vehicleStatus;
        Boolean isAccedentFree = null;
        MaintenanceScore maintenanceScore = null;
        try {
            MaintenanceFrequency maintenanceFrequency = null;
            if (features != null && features.contains(Feature.ACCIDENT_FREE.toString())) {
                Insurance insurance = getInsurance(vin);
                isAccedentFree = insurance != null ? insurance.getReport().getClaims() == 0 : null;
            }
            if (features != null && features.contains(Feature.MAINTANANCE.toString())) {
                maintenanceFrequency = getMaintenanceFrequency(vin);
                if (maintenanceFrequency != null && maintenanceFrequency.getMaintenanceFrequency() != null) {
                    if (maintenanceFrequency.getMaintenanceFrequency().toLowerCase().contains("low")) {
                        maintenanceScore = MaintenanceScore.POOR;
                    } else if (maintenanceFrequency.getMaintenanceFrequency().toLowerCase().contains("medium")) {
                        maintenanceScore = MaintenanceScore.AVERAGE;
                    } else if (maintenanceFrequency.getMaintenanceFrequency().toLowerCase().contains("high")) {
                        maintenanceScore = MaintenanceScore.GOOD;
                    }
                }
            }
            vehicleStatus = new VehicleStatus(vin, maintenanceScore, isAccedentFree);
            lastVehicleStatus.set(vehicleStatus);
            return vehicleStatus;
        } catch (RuntimeException e) {
            LOG.error("Error during getting data for vin = {}", vin, e);
            throw new ServiceUnavailableException(e);
        }
    }

    public Optional<VehicleStatus> getLastVehicleStatus() {
        return Optional.ofNullable(lastVehicleStatus.get());
    }

    Insurance getInsurance(String vin) {
        String path = "/accidents/report?vin=";
        String url;
        try {
            URI uri = new URI(insuranceEndpoint).resolve(path);
            url = uri.toString();
        } catch (URISyntaxException e) {
            url = insuranceEndpoint + path;
            e.printStackTrace();
        }
        HttpRequest<String> request = HttpRequest.GET(url + vin);
        try {
            return httpClient.toBlocking().retrieve(request, Insurance.class);
        } catch (RuntimeException e) {
            LOG.error("Insurance request failure", e);
            throw e;
        }
    }

    MaintenanceFrequency getMaintenanceFrequency(String vin) {
        String path = "/cars/";
        String url;
        try {
            URI uri = new URI(maintenanceFrequencyEndpoint).resolve(path);
            url = uri.toString();
        } catch (URISyntaxException e) {
            url = maintenanceFrequencyEndpoint + path;
            e.printStackTrace();
        }

        HttpRequest<String> request = HttpRequest.GET(url + vin);

        try {
            return httpClient.toBlocking().retrieve(request, MaintenanceFrequency.class);
        } catch (RuntimeException e) {
            LOG.error("MaintenanceFrequency request failure", e);
            throw e;
        }
    }

}
