package com.softavail.examination;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.softavail.examination.model.VehicleStatus;

@Component
public class VehicleStatusJob {
    private static final Logger LOG = LoggerFactory.getLogger(VehicleStatusJob.class);

    private final VehicleStatusService vehicleStatusService;

    public VehicleStatusJob(VehicleStatusService vehicleStatusService) {
        this.vehicleStatusService = vehicleStatusService;
    }

    @Scheduled(fixedDelayString = "1s")
    void printLastVehicleStatus() {
        final Optional<VehicleStatus> vehicleStatus = vehicleStatusService.getLastVehicleStatus();
        vehicleStatus.ifPresent(vim -> {
            LOG.info("Last Vehicle status was = {}", vehicleStatus.get().getVin());
        });
    }
}
