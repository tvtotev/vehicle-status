package com.softavail.examination;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import reactor.core.publisher.Mono;

@Controller ("/")
public class HomeController {

    @Get(produces = MediaType.TEXT_HTML)
    public Mono<String> home() {
        return Mono.just("Welcome to Vehicle Status");
    }

}
