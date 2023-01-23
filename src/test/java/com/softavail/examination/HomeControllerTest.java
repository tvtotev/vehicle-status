package com.softavail.examination;

import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class HomeControllerTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void renderServerSideHTMLwithThymeleafAndMicronautViews() {
        String expected = "<!DOCTYPE html>\n" +
                "<html>\n"+
                "<head>\n"+
                "    <title>Home</title>\n"+
                "</head>\n"+
                "<body>\n"+
                "    <h1>Welcome to Vehicle Status</h1>\n"+
                "</body>\n"+
                "</html>";
        String html = httpClient.toBlocking().retrieve("/");
        assertEquals(expected, html);
    }
}
