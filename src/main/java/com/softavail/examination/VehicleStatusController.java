package com.softavail.examination;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;

@RestController
public class VehicleStatusController {

    private final VehicleStatusService vehicleStatusService;

    public VehicleStatusController(VehicleStatusService vehicleStatusService) {
        this.vehicleStatusService = vehicleStatusService;
    }

    @GetMapping("/check")
    public VehicleStatus check(@RequestParam(value = "vin", defaultValue = "") String vin) {
        return vehicleStatusService.check(vin);
    }

    @PostMapping("/check")
    public VehicleStatus check(@RequestBody VehicleStatusRequest vehicleStatusRequest) {
        return vehicleStatusService.check(vehicleStatusRequest.getVin(), vehicleStatusRequest.getFeatures());
    }
}