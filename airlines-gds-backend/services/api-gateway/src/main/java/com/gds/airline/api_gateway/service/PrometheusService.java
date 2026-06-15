package com.gds.airline.api_gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PrometheusService {

    private final WebClient webClient;

    public PrometheusService(WebClient.Builder webClientBuilder) {
        // Assuming Prometheus is mapped to port 9090 on localhost
        this.webClient = webClientBuilder.baseUrl("http://localhost:9090").build();
    }

    public Mono<Double> queryMetric(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/query")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> {
                    try {
                        JsonNode resultNode = jsonNode.path("data").path("result");
                        if (resultNode.isArray() && resultNode.size() > 0) {
                            JsonNode valueNode = resultNode.get(0).path("value");
                            if (valueNode.isArray() && valueNode.size() == 2) {
                                return Double.parseDouble(valueNode.get(1).asText());
                            }
                        }
                    } catch (Exception e) {
                        // In case of parsing error, return 0.0 or handle appropriately
                    }
                    return 0.0;
                })
                .defaultIfEmpty(0.0)
                .onErrorReturn(0.0);
    }
}
