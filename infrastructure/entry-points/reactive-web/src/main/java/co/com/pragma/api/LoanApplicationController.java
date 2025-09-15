package co.com.pragma.api;

import co.com.pragma.api.dto.*;
import co.com.pragma.api.handler.LoanApplicationHandler;
import co.com.pragma.model.loan_application.util.LoanApplicationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
@Tag(name = "LoanApplicationController", description = "Entrada para las operaciones relacionadas al modelo de solicitud")
@Validated
public class LoanApplicationController {

    private final LoanApplicationHandler loanApplicationHandler;

    @PostMapping(value = "/application", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @Operation(summary = "Agregar solicitud", description = "Permite recibir una petición de agregar una solicitud. Este evalua los campos obligatorios, existencia y formatos para antes de crear el elemento en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "solicitud creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos recibidos no cumplen con la obligatoriedad o formatos esperados", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error inesperado durante el proceso", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class)))})
    public Mono<ResponseEntity<GenericResponseDTO<Object>>> saveLoanApplication(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateLoanApplicationDTO createLoanApplicationDTO) {
        return loanApplicationHandler.createLoanApplication(createLoanApplicationDTO, authHeader)
                .map(genericResponseDto -> ResponseEntity.status(genericResponseDto.getResponseCode()).body(genericResponseDto));

    }

    @PutMapping(value = "/application", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADVISER', 'ROLE_ADMIN')")
    @Operation(summary = "Actualizar solicitud", description = "Permite recibir una petición de actualizar una solicitud. Este evalua los campos obligatorios, existencia y formatos para antes de crear el elemento en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "solicitud actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos recibidos no cumplen con la obligatoriedad o formatos esperados", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error inesperado durante el proceso", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class)))})
    public Mono<ResponseEntity<GenericResponseDTO<Object>>> updateLoanApplication(
            @Valid @RequestBody UpdateLoanApplicationDTO updateLoanApplicationDTO) {
        return loanApplicationHandler.updateLoanAplications(updateLoanApplicationDTO)
                .map(genericResponseDto -> ResponseEntity.status(genericResponseDto.getResponseCode()).body(genericResponseDto));

    }

    @GetMapping(value = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADVISER')")
    @Operation(summary = "Listar solicitudes", description = "Permite recibir parámetros para realizar filtro a la lista de solicitudes en la app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "El servicio responde correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos recibidos no cumplen con la obligatoriedad o formatos esperados", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encuentran registros con los datos ingresados", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error inesperado durante el proceso", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class)))})
    public Mono<ResponseEntity<GenericResponseDTO<LoanApplicationPageListDTO>>> listLoanApplications(
            @RequestParam(name = "status") @NotNull(message = "El estado es requerido.") LoanApplicationStatus status,
            @RequestParam(name = "page", defaultValue = "1") @Min(value = 1, message = "El número de página debe ser mayor a 0") int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(value = 1, message = "El tamaño de página debe ser mayor a 0") int size
    ) {
        return loanApplicationHandler.listLoanApllications(status, page, size)
                .map(genericResponseDto -> ResponseEntity.status(genericResponseDto.getResponseCode()).body(genericResponseDto));
    }

    @GetMapping(value = "/applications/by-email-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_API_KEY', 'ROLE_ADMIN')")
    @Operation(summary = "Listar solicitudes", description = "Permite recibir parámetros para realizar filtro a la lista de solicitudes en la app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "El servicio responde correctamente"),
            @ApiResponse(responseCode = "400", description = "Los datos recibidos no cumplen con la obligatoriedad o formatos esperados", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encuentran registros con los datos ingresados", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error inesperado durante el proceso", content = @Content(schema = @Schema(implementation = GenericResponseDTO.class)))})
    public Mono<ResponseEntity<GenericResponseDTO<Object>>> listLoanApplicationsByEmailAndStatus(
            @RequestParam(name = "email") @Email(message = "El estado es requerido.") String email,
            @RequestParam(name = "status") @NotNull(message = "El estado es requerido.") LoanApplicationStatus status
    ) {
        return loanApplicationHandler.listLoanApllicationsByEmailAndStatus(email, status)
                .map(genericResponseDto -> ResponseEntity.status(genericResponseDto.getResponseCode()).body(genericResponseDto));
    }

}