package org.example.spring1.twelveData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TwelveDataService {

    private final WebClient webClient;

    public TwelveDataService(@Value("${twelve.data.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://twelve-data1.p.rapidapi.com")
                .defaultHeader("X-RapidAPI-Key", apiKey)
                .defaultHeader("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                .build();
    }

    public Mono<Double> findStockPrice(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/price")
                        .queryParam("symbol", symbol)
                        .queryParam("format", "json")
                        .queryParam("outputsize", "30")
                        .build())
                .retrieve()
                .bodyToMono(PriceResponse.class)
                .map(response -> response.getPrice());
    }

    private static class PriceResponse {
        private Double price;

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }
}
