package org.ikigaidigital.api;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.api.dto.TimeDepositResponse;
import org.ikigaidigital.api.dto.WithdrawalResponse;
import org.ikigaidigital.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.persistence.entity.WithdrawalEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class TimeDepositMapper {
    public TimeDepositResponse toResponse(
        TimeDepositEntity deposit,
        List<WithdrawalEntity> withdrawals
    ) {
        return new TimeDepositResponse(
            deposit.getId(),
            deposit.getPlanType(),
            deposit.getBalance(),
            deposit.getDays(),
            withdrawals.stream()
                .map(withdrawal -> new WithdrawalResponse(
                    withdrawal.getId(),
                    withdrawal.getAmount(),
                    withdrawal.getDate()
                ))
                .toList()
        );
    }

    public TimeDeposit toDomain(TimeDepositEntity entity) {
        return new TimeDeposit(
            entity.getId(),
            entity.getPlanType(),
            entity.getBalance().doubleValue(),
            entity.getDays()
        );
    }

    public void updateBalance(TimeDepositEntity entity, TimeDeposit deposit) {
        entity.setBalance(BigDecimal.valueOf(deposit.getBalance()).setScale(2, RoundingMode.HALF_UP));
    }
}
