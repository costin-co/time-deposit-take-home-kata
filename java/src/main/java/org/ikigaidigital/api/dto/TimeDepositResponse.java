package org.ikigaidigital.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record TimeDepositResponse(
    Integer id,
    String planType,
    BigDecimal balance,
    Integer days,
    List<WithdrawalResponse> withdrawals
) {
}
