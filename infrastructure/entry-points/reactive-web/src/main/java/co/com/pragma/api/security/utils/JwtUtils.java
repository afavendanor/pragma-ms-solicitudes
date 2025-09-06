package co.com.pragma.api.security.utils;

import co.com.pragma.model.error.LoginException;
import co.com.pragma.model.error.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public Mono<String> getClaim(String token, String startsWith) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = mapper.readValue(payload, Map.class);

            Object authorities = claims.get("authorities");

            if (authorities instanceof List<?> roles) {
                Optional<String> value = roles.stream()
                        .map(Object::toString)
                        .filter(v -> v.startsWith(startsWith))
                        .findFirst();

                return value.map(v -> {
                            if (startsWith.equals("ID_")) {
                                return v.substring(3);
                            }
                            return v;
                        })
                        .map(Mono::just)
                        .orElse(Mono.empty());
            }

            return Mono.empty();

        } catch (Exception e) {
            return Mono.error(new LoginException(ResponseCode.MSSO007, "Error parsing token"));
        }
    }


}