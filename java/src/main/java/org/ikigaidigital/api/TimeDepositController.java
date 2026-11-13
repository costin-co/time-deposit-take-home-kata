package org.ikigaidigital.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ikigaidigital.api.dto.TimeDepositResponse;
import org.ikigaidigital.service.TimeDepositService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/time-deposits")
public class TimeDepositController {
    private final TimeDepositService timeDepositService;

    public TimeDepositController(TimeDepositService timeDepositService) {
        this.timeDepositService = timeDepositService;
    }

    @GetMapping
    @Operation(
        summary = "List time deposits",
        description = "Returns all time deposits, including each deposit's associated withdrawals.",
        responses = @ApiResponse(
            responseCode = "200",
            description = "Time deposits retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = TimeDepositResponse.class))
            )
        )
    )
    public List<TimeDepositResponse> findAll() {
        return timeDepositService.findAll();
    }

    @PostMapping("/update-balances")
    @Operation(
        summary = "Update all time deposit balances",
        description = "Applies the existing interest calculation to every persisted time deposit and saves the results.",
        responses = @ApiResponse(responseCode = "204", description = "All balances updated successfully")
    )
    public ResponseEntity<Void> updateBalances() {
        timeDepositService.updateAllBalances();
        return ResponseEntity.noContent().build();
    }
}
