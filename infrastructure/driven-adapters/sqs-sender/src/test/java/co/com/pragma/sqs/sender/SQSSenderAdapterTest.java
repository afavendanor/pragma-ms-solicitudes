package co.com.pragma.sqs.sender;

import co.com.pragma.model.loan_application.LoanApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSSenderAdapterTest {

    @Mock
    private SqsAsyncClient client;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SQSSenderProperties properties;

    @InjectMocks
    private SQSSenderAdapter sqsSenderAdapter;

    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        loanApplication = new LoanApplication();
        when(properties.queueUrl()).thenReturn("http://fake-queue-url");
    }

    @Test
    void shouldSendMessageSuccessfully() throws Exception {
        // Act
        String json = "{\"id\":1}";
        when(objectMapper.writeValueAsString(loanApplication)).thenReturn(json);

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("12345")
                .build();

        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // Act & Assert
        StepVerifier.create(sqsSenderAdapter.send(loanApplication))
                .expectNext("12345")
                .verifyComplete();
    }

    @Test
    void shouldFailWhenJsonProcessingException() throws Exception {
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("error") {});

        StepVerifier.create(sqsSenderAdapter.send(loanApplication))
                .expectError(JsonProcessingException.class)
                .verify();
    }

    @Test
    void shouldFailWhenSqsClientFails() throws Exception {
        String json = "{\"id\":1}";
        when(objectMapper.writeValueAsString(loanApplication)).thenReturn(json);

        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("SQS down")));

        StepVerifier.create(sqsSenderAdapter.send(loanApplication))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("SQS down"))
                .verify();
    }
}
