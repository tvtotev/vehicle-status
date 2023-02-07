package com.softavail.examination.clients;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

import com.softavail.examination.model.VehicleStatus;
import com.softavail.examination.model.VehicleStatusRequest;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client("/check")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = ACCEPT, value = MediaType.APPLICATION_JSON)
public interface VehicleStatusClient {

    @Get(uri = "/", produces = MediaType.APPLICATION_JSON)
    Mono<VehicleStatus> check(@Nullable @QueryValue("vin") String vin);

    @Post(uri = "/", produces = MediaType.APPLICATION_JSON)
    Mono<VehicleStatus> check(@NonNull @Body VehicleStatusRequest vehicleStatusCheckReques);
}
