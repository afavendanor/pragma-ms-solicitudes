package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.CreateSolicitudeDTO;
import co.com.pragma.model.solicitude.Solicitude;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SolicitudeApiRestMapper {

    Solicitude createSolicitudeDTOToSolicitude(CreateSolicitudeDTO createSolicitudeDTO);

}
