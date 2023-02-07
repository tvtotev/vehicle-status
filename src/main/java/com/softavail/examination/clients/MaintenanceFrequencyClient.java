package com.softavail.examination.clients;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

import org.reactivestreams.Publisher;

import com.softavail.examination.model.MaintenanceFrequency;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;

@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = ACCEPT, value = MediaType.APPLICATION_JSON)
@Client(InsuranceClient.SERVICE_NAME)
public interface MaintenanceFrequencyClient {

    public static final String SERVICE_NAME = "maintenance-frequency";

    @Get(uri = "/cars/{vin}", produces = MediaType.APPLICATION_JSON)
    Publisher<MaintenanceFrequency> cars(@Nullable @PathVariable("vin") String vin);

}
