package co.com.pragma.sns.sender;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sns")
public record SNSSenderProperties(
     String region,
     String topicArn) {
}
