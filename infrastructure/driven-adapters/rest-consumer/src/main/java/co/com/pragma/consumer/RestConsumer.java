package co.com.pragma.consumer;

import co.com.pragma.model.error.DependencyException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserRepository {

    @Value("${adapter.rest-consumer.api-key}")
    private String apiKey;

    private final WebClient client;

    @Override
    public Mono<List<User>> findAllByEmails(List<String> emails) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/emails")
                        .queryParam("emails", String.join(",", emails))
                        .build())
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ObjectResponse<List<User>>>() {})
                .map(ObjectResponse::getData)
                .onErrorMap(error -> new DependencyException(ResponseCode.MSSO009));
    }

}
