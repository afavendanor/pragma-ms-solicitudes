package co.com.pragma.api.utils;

import co.com.pragma.model.error.LoginException;
import co.com.pragma.model.error.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;

@Component
public class JwtUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public Mono<String> getClaim(String token, String claimName) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map claims = mapper.readValue(payload, Map.class);

            Object value = claims.get(claimName);
            return value != null
                    ? Mono.just(value.toString())
                    : Mono.empty();
        } catch (Exception e) {
            return Mono.error(new LoginException(ResponseCode.MSSO007, "Error parsing token"));
        }
    }

}