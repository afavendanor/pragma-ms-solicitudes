package co.com.pragma.api;

import co.com.pragma.api.dto.CreateLoanApplicationDTO;
import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.api.handler.LoanApplicationHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

}