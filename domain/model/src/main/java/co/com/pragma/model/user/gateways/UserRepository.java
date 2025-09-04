package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {

    Mono<List<User>> findAllByEmails(List<String> emails);

}
