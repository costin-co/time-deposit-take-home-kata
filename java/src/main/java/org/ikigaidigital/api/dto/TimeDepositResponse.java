package org.ikigaidigital.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "TimeDeposit", description = "A time deposit and its associated withdrawals.")
public record TimeDepositResponse(
    @Schema(description = "Unique time deposit identifier", example = "1")
    Integer id,
    @Schema(description = "Deposit plan type", example = "basic")
    String planType,
    @Schema(description = "Current balance with two decimal places", example = "1000.00")
    BigDecimal balance,
    @Schema(description = "Number of days the deposit has been active", example = "31")
    Integer days,
    @Schema(description = "Withdrawals associated with this deposit")
    List<WithdrawalResponse> withdrawals
) {
}
