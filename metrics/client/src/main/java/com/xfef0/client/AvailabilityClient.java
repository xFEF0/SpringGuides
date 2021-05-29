package com.xfef0.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AvailabilityClient {

    private static final String URI = "http://localhost:8083/availability/{console}";
    private final WebClient webClient;

    Mono<Availability> checkAvailability(String console) {
        return this.webClient
                .get()
                .uri(URI)
                .retrieve()
                .bodyToMono(Availability.class)
                .onErrorReturn(new Availability(false, console));
    }
}
