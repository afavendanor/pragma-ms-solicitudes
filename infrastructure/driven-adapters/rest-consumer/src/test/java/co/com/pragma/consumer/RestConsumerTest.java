package co.com.pragma.consumer;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

class RestConsumerTest {

    private static RestConsumer restConsumer;

    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        restConsumer = new RestConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {

        mockBackEnd.shutdown();
    }

    @Test
    void validateTestGet() {

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"data\":[{\"identification\":\"CC123456788\",\"name\":\"Andres\",\"lastName\":\"Avendaño\",\"birthDay\":\"1991-05-14\",\"address\":\"Calle 123 #45-67, Bogotá\",\"phone\":\"+57 3001234567\",\"email\":\"andres.avendano@example.com\",\"baseSalary\":4500000}]}"));
        var response = restConsumer.findAllByEmails(List.of("mail"));

        StepVerifier.create(response)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

}