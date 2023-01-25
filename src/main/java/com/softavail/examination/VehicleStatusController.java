package com.softavail.examination;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softavail.examination.model.VehicleStatusRequest;

@RestController
public class VehicleStatusController {

    private final VehicleStatusService vehicleStatusService;

    public VehicleStatusController(VehicleStatusService vehicleStatusService) {
        this.vehicleStatusService = vehicleStatusService;
    }

    @GetMapping("/check")
    public ResponseEntity<?>  check(@RequestParam(value = "vin", defaultValue = "") String vin) {
        return ResponseEntity.ok(vehicleStatusService.check(vin));
    }

    @PostMapping("/check")
    public ResponseEntity<?>  check(@RequestBody VehicleStatusRequest vehicleStatusRequest) {
        try {
            return ResponseEntity.ok(vehicleStatusService.check(vehicleStatusRequest.getVin(), vehicleStatusRequest.getFeatures()));
        } catch (ServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service is temporary unavailable (CODE 503)\n");
        }
    }
}