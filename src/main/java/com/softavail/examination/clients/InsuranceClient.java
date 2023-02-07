package com.softavail.examination.clients;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

import com.softavail.examination.model.Insurance;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = ACCEPT, value = MediaType.APPLICATION_JSON)
@Client(InsuranceClient.SERVICE_NAME)
public interface InsuranceClient {

    public static final String SERVICE_NAME = "insurance";

    @Get(uri = "/accidents/report", produces = MediaType.APPLICATION_JSON)
    Mono<Insurance> accidentReport(@Nullable @QueryValue("vin") String vin);

}
