package org.ikigaidigital.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WithdrawalResponse(Integer id, BigDecimal amount, LocalDate date) {
}
