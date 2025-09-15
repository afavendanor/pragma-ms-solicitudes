package co.com.pragma.sns.sender;

import co.com.pragma.model.error.InternalErrorException;
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
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SNSSenderAdapterTest {

    @Mock
    private SnsAsyncClient client;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SNSSenderProperties properties;

    @InjectMocks
    private SNSSenderAdapter snsSenderAdapter;

    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        loanApplication = new LoanApplication();
        when(properties.topicArn()).thenReturn("http://fake-topic-arn");
    }

    @Test
    void shouldSendMessageSuccessfully() throws Exception {
        // Act
        String json = "{\"id\":1}";
        when(objectMapper.writeValueAsString(loanApplication)).thenReturn(json);

        PublishResponse response = PublishResponse.builder()
                .messageId("12345")
                .build();

        when(client.publish(any(PublishRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // Act & Assert
        StepVerifier.create(snsSenderAdapter.send(loanApplication, "NOTIFY"))
                .expectNext("12345")
                .verifyComplete();
    }

    @Test
    void shouldFailWhenJsonProcessingException() throws Exception {
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("error") {});

        StepVerifier.create(snsSenderAdapter.send(loanApplication, "NOTIFY"))
                .expectError(InternalErrorException.class)
                .verify();
    }

    @Test
    void shouldFailWhenSnsClientFails() throws Exception {
        String json = "{\"id\":1}";
        when(objectMapper.writeValueAsString(loanApplication)).thenReturn(json);

        when(client.publish(any(PublishRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("SNS down")));

        StepVerifier.create(snsSenderAdapter.send(loanApplication, "CAPACITY"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("SNS down"))
                .verify();
    }
}
