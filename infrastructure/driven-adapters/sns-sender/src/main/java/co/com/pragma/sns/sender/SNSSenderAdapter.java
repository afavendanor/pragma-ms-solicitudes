package co.com.pragma.sns.sender;

import co.com.pragma.model.error.InternalErrorException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.gateways.LoanApplicationSNSSenderGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SNSSenderAdapter implements LoanApplicationSNSSenderGateway {

    private static final Logger log = Loggers.getLogger(SNSSenderAdapter.class.getName());

    private final SNSSenderProperties properties;
    private final SnsAsyncClient client;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<String> send(LoanApplication message, String notificationType) {

        return Mono.fromFuture(() -> {
                    try {
                        return client.publish(buildRequest(message, notificationType));
                    } catch (JsonProcessingException e) {
                        throw new InternalErrorException(ResponseCode.MSSO000);
                    }
                })
                .map(PublishResponse::messageId)
                .doOnSuccess(id -> log.debug("Message sent {}", id))
                .doOnError(error -> log.error("Message send error: {}", error.getMessage(), error));
    }

    private PublishRequest buildRequest(LoanApplication message, String notificationType) throws JsonProcessingException {
        return PublishRequest.builder()
                .topicArn(properties.topicArn())
                .message(objectMapper.writeValueAsString(message))
                .messageAttributes(Map.of(
                        "type", MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue(notificationType)
                                .build()
                ))
                .build();
    }

}
