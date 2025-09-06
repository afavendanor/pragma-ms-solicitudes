package co.com.pragma.sqs.sender;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.gateways.LoanApplicationSQSSenderGateway;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@RequiredArgsConstructor
public class SQSSenderAdapter implements LoanApplicationSQSSenderGateway {

    private static final Logger log = Loggers.getLogger(SQSSenderAdapter.class.getName());

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<String> send(LoanApplication message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(LoanApplication message) throws JsonProcessingException {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(objectMapper.writeValueAsString(message))
                .build();
    }
}
