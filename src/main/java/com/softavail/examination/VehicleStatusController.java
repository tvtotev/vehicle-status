package com.softavail.examination;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.scheduling.annotation.Async;
import reactor.core.publisher.Mono;

@Controller("/check")
public class VehicleStatusController {

    private final VehicleStatusService vehicleStatusService;

    public VehicleStatusController(VehicleStatusService vehicleStatusService) {
        this.vehicleStatusService = vehicleStatusService;
    }

    @Async
    @Get("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<VehicleStatus> check(
            @NotBlank(message = "VIN cannot be empty/nill") @Size(min = 3, max = 64, message = "VIN Length must be 3-64 chars") @QueryValue("vin") String vin) {
        return Mono.just(vehicleStatusService.check(vin));
    }

    @Async
    @Post("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<VehicleStatus> check(@Body @NotNull VehicleStatusRequest vehicleStatusRequest) {
        try {
            return Mono.just(
                    vehicleStatusService.check(vehicleStatusRequest.getVin(), vehicleStatusRequest.getFeatures()));
        } catch (ServiceUnavailableException e) {
            return Mono.error(new HttpStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Service is temporary unavailable (CODE 503)\n"));
        }
    }

    /*
     * @Async
     * 
     * @Get("/check") public Mono<VehicleStatus> check( @RequestParam(value = "vin",
     * defaultValue = "") String vin) { return
     * Mono.just(vehicleStatusService.check(vin)); }
     * 
     * @Async
     * 
     * @Post("/check") public ResponseEntity<Mono<?>> check(@RequestBody
     * VehicleStatusRequest vehicleStatusRequest) { try { return
     * Mono.just(vehicleStatusService.check(vehicleStatusRequest.getVin(),
     * vehicleStatusRequest.getFeatures())); } catch (ServiceUnavailableException e)
     * { return Mono.just(null)
     * 
     * Mono.error(new WebClientExceptio
     * Mono.error("Service is temporary unavailable (CODE 503)", 503); } }
     */
}