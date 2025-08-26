package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterSolicitudeUseCase {

    private final SolicitudeRepository solicitudeRepository;

    public Mono<Solicitude> execute(Solicitude solicitude) {
        return solicitudeRepository.save(solicitude);
    }

}
