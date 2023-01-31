package com.softavail.examination;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
class HomeControllerTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void renderServerSideHTMLwithThymeleafAndMicronautViews() {
        String expected = "Welcome to Vehicle Status";
        String html = httpClient.toBlocking().retrieve("/");
        assertEquals(expected, html);
    }

}
