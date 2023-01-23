package com.softavail.examination;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.client.annotation.Client;

@Client("/")
public interface VehicleStatusClient {

    @GetMapping("/check{?vin}")
    VehicleStatus check(@Nullable String vin);

    @PostMapping("/check")
    VehicleStatus check(@RequestBody VehicleStatusRequest vehicleStatusCheckReques);
}
