package com.softavail.examination;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;

import io.micronaut.scheduling.annotation.Async;

@RestController
public class VehicleStatusController {

    private final VehicleStatusService vehicleStatusService;

    public VehicleStatusController(VehicleStatusService vehicleStatusService) {
        this.vehicleStatusService = vehicleStatusService;
    }

    @Async
    @GetMapping("/check")
    public CompletableFuture<ResponseEntity<VehicleStatus>> check(
            @RequestParam(value = "vin", defaultValue = "") String vin) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(vehicleStatusService.check(vin)));
    }

    @Async
    @PostMapping("/check")
    public CompletableFuture<ResponseEntity<?>> check(@RequestBody VehicleStatusRequest vehicleStatusRequest) {
        try {
            return CompletableFuture.completedFuture(ResponseEntity
                    .ok(vehicleStatusService.check(vehicleStatusRequest.getVin(), vehicleStatusRequest.getFeatures())));
        } catch (ServiceUnavailableException e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Service is temporary unavailable (CODE 503)\n"));
        }
    }
}