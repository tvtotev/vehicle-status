package com.softavail.examination;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import com.softavail.examination.model.Insurance;
import com.softavail.examination.model.MaintenanceFrequency;
import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatus.MaintenanceScore;
import com.softavail.examination.model.VehicleStatusRequest.Feature;

import io.micronaut.context.annotation.Property;

@Service
@Validated
public class VehicleStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleStatusService.class);

    private final AtomicReference<VehicleStatus> lastVehicleStatus = new AtomicReference<>();

    @Property(name = "endpoint.insurance")
    private String insuranceEndpoint;

    @Property(name = "endpoint.maintenance.frequency")
    private String maintenanceFrequencyEndpoint;

    public VehicleStatus check(String vin) {
        final VehicleStatus vehicleStatus = new VehicleStatus(vin, null, false);
        lastVehicleStatus.set(vehicleStatus);
        return vehicleStatus;
    }

    public VehicleStatus check(String vin, Set<String> features) {
        final VehicleStatus vehicleStatus;
        boolean isAccedentFree = false;
        MaintenanceScore maintenanceScore = null;
        try {
            MaintenanceFrequency maintenanceFrequency = null;
            if (features.contains(Feature.ACCIDENT_FREE.toString())) {
                Insurance insurance = getInsurance(vin);
                isAccedentFree = insurance != null ? insurance.getReport().getClaims() == 0 : null;
            }
            if (features.contains(Feature.ACCIDENT_FREE.toString())) {
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
            throw e;
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
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseStr = restTemplate.getForEntity(url + vin, String.class);
        LOG.info(url + vin + "=" + responseStr);
        ResponseEntity<Insurance> response;
        try {
            response = restTemplate.getForEntity(url + vin, Insurance.class);
            return response.getBody();
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

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseStr = restTemplate.getForEntity(url + vin, String.class);
        LOG.info(url + vin + "=" + responseStr);
        ResponseEntity<MaintenanceFrequency> response;
        try {
            response = restTemplate.getForEntity(url + vin, MaintenanceFrequency.class);
            return response.getBody();
        } catch (RuntimeException e) {
            LOG.error("MaintenanceFrequency request failure", e);
            throw e;
        }
    }

}
