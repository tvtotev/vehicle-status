package com.softavail.examination;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.softavail.examination.model.VehicleStatus;

@Service
@Validated
public class VehicleStatusService {

    private final AtomicReference<VehicleStatus> lastVehicleStatus = new AtomicReference<>();

    public VehicleStatus check(String vin) {
        final VehicleStatus vehicleStatus = new VehicleStatus(vin, null);
        lastVehicleStatus.set(vehicleStatus);
        return vehicleStatus;
    }

    public VehicleStatus check(String vin, Set<String> features) {
        final VehicleStatus vehicleStatus = new VehicleStatus(vin, null);
        lastVehicleStatus.set(vehicleStatus);
        return vehicleStatus;
    }

    public Optional<VehicleStatus> getLastVehicleStatus() {
        return Optional.ofNullable(lastVehicleStatus.get());
    }
}
