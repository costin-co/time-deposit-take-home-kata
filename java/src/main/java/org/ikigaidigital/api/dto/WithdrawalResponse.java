package org.ikigaidigital.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "Withdrawal", description = "A withdrawal made from a time deposit.")
public record WithdrawalResponse(Integer id, BigDecimal amount, LocalDate date) {
}
